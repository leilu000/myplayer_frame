package player.videocache;

import android.content.Context;
import android.net.Uri;
import android.util.Log;


import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import player.videocache.file.DiskUsage;
import player.videocache.file.FileNameGenerator;
import player.videocache.file.Md5FileNameGenerator;
import player.videocache.file.TotalCountLruDiskUsage;
import player.videocache.file.TotalSizeLruDiskUsage;
import player.videocache.headers.EmptyHeadersInjector;
import player.videocache.headers.HeaderInjector;
import player.videocache.sourcestorage.SourceInfoStorage;
import player.videocache.sourcestorage.SourceInfoStorageFactory;


public class HttpProxyCacheServer {
    private static final String PROXY_HOST = "127.0.0.1";
    private final Object clientsLock = new Object();
    private final ExecutorService socketProcessor = Executors.newFixedThreadPool(8);
    private final Map<String, HttpProxyCacheServerClients> clientsMap = new ConcurrentHashMap<>();

    private final ServerSocket serverSocket;
    private final int port;
    private final Thread waitConnectionThread;
    private final Config config;
    private final Pinger pinger;

    public HttpProxyCacheServer(Context context) {
        this((new Builder(context)).buildConfig());
    }


    private HttpProxyCacheServer(Config config) {
        this.config = Preconditions.checkNotNull(config);
        try {
            InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
            this.serverSocket = new ServerSocket(0, 8, inetAddress);
            this.port = this.serverSocket.getLocalPort();
            IgnoreHostProxySelector.install("127.0.0.1", this.port);
            CountDownLatch startSignal = new CountDownLatch(1);
            this.waitConnectionThread = new Thread(new WaitRequestsRunnable(startSignal));
            this.waitConnectionThread.start();
            startSignal.await();
            this.pinger = new Pinger("127.0.0.1", this.port);
            Log.i("leilu", "Proxy cache server started. Is it alive? " + isAlive());
        } catch (IOException | InterruptedException e) {
            this.socketProcessor.shutdown();
            throw new IllegalStateException("Error starting local proxy server", e);
        }
    }


    public String getProxyUrl(String url) {
        return getProxyUrl(url, true);
    }


    public String getProxyUrl(String url, boolean allowCachedFileUri) {
        Log.i("leilu", "VideoCache-->getProxyUrl,url:" + url + "  allowCachedFileUri:" + allowCachedFileUri);
        if (allowCachedFileUri && isCached(url) && !url.endsWith(".m3u8")) {
            File cacheFile = getCacheFile(url);
            touchFileSafely(cacheFile);
            Log.i("leilu", "VideoCache-->getProxyUrl,cached path:" + cacheFile.getAbsolutePath());
            return Uri.fromFile(cacheFile).toString();
        }
        return isAlive() ? appendToProxyUrl(url) : url;
    }

    public void registerCacheListener(CacheListener cacheListener, String url) {
        Preconditions.checkAllNotNull(new Object[]{cacheListener, url});
        synchronized (this.clientsLock) {
            try {
                getClients(url).registerCacheListener(cacheListener);
            } catch (ProxyCacheException e) {
                Log.i("leilu", "Error registering cache listener", e);
            }
        }
    }

    public void unregisterCacheListener(CacheListener cacheListener, String url) {
        Preconditions.checkAllNotNull(new Object[]{cacheListener, url});
        synchronized (this.clientsLock) {
            try {
                getClients(url).unregisterCacheListener(cacheListener);
            } catch (ProxyCacheException e) {
                Log.i("leilu", "Error registering cache listener", e);
            }
        }
    }

    public void unregisterCacheListener(CacheListener cacheListener) {
        Preconditions.checkNotNull(cacheListener);
        synchronized (this.clientsLock) {
            for (HttpProxyCacheServerClients clients : this.clientsMap.values()) {
                clients.unregisterCacheListener(cacheListener);
            }
        }
    }


    public boolean isCached(String url) {
        Preconditions.checkNotNull(url, "Url can't be null!");
        return getCacheFile(url).exists();
    }

    public void shutdown() {
        Log.i("leilu", "Shutdown proxy server");

        shutdownClients();

        this.config.sourceInfoStorage.release();

        this.waitConnectionThread.interrupt();
        try {
            if (!this.serverSocket.isClosed()) {
                this.serverSocket.close();
            }
        } catch (IOException e) {
            onError(new ProxyCacheException("Error shutting down proxy server", e));
        }
    }


    private boolean isAlive() {
        return this.pinger.ping(3, 70);
    }


    private String appendToProxyUrl(String url) {
        return String.format(Locale.US, "http://%s:%d/%s", new Object[]{"127.0.0.1", Integer.valueOf(this.port), ProxyCacheUtils.encode(url)});
    }


    private File getCacheFile(String url) {
        File cacheDir = this.config.cacheRoot;
        String fileName = this.config.fileNameGenerator.generate(url);
        return new File(cacheDir, fileName);
    }

    private void touchFileSafely(File cacheFile) {
        try {
            this.config.diskUsage.touch(cacheFile);
        } catch (IOException e) {
            Log.i("leilu", "Error touching file " + cacheFile, e);
        }
    }

    private void shutdownClients() {
        synchronized (this.clientsLock) {
            for (HttpProxyCacheServerClients clients : this.clientsMap.values()) {
                clients.shutdown();
            }
            this.clientsMap.clear();
        }
    }

    private void waitForRequest() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = this.serverSocket.accept();
                Log.d("leilu", "Accept new socket " + socket);
                this.socketProcessor.submit(new SocketProcessorRunnable(socket));
            }
        } catch (IOException e) {
            onError(new ProxyCacheException("Error during waiting connection", e));
        }
    }

    private void processSocket(Socket socket) {
        try {
            GetRequest request = GetRequest.read(socket.getInputStream());
            Log.d("leilu", "VideoCache-->processSocket:" + request);
            String url = ProxyCacheUtils.decode(request.uri);
            if (this.pinger.isPingRequest(url)) {
                this.pinger.responseToPing(socket);
            } else {
                HttpProxyCacheServerClients clients = getClients(url);
                clients.processRequest(request, socket);
            }
        } catch (SocketException e) {
            Log.d("leilu", "Closing socket… Socket is closed by client.");
        } catch (ProxyCacheException | IOException e) {
            onError(new ProxyCacheException("Error processing request", e));
        } finally {
            releaseSocket(socket);
            Log.d("leilu", "Opened connections: " + getClientsCount());
        }
    }

    private HttpProxyCacheServerClients getClients(String url) throws ProxyCacheException {
        synchronized (this.clientsLock) {
            HttpProxyCacheServerClients clients = this.clientsMap.get(url);
            if (clients == null) {
                clients = new HttpProxyCacheServerClients(url, this.config);
                this.clientsMap.put(url, clients);
            }
            return clients;
        }
    }

    private int getClientsCount() {
        synchronized (this.clientsLock) {
            int count = 0;
            for (HttpProxyCacheServerClients clients : this.clientsMap.values()) {
                count += clients.getClientsCount();
            }
            return count;
        }
    }

    private void releaseSocket(Socket socket) {
        closeSocketInput(socket);
        closeSocketOutput(socket);
        closeSocket(socket);
    }

    private void closeSocketInput(Socket socket) {
        try {
            if (!socket.isInputShutdown()) {
                socket.shutdownInput();
            }
        } catch (SocketException e) {
            Log.d("leilu", "Releasing input stream… Socket is closed by client.");
        } catch (IOException e) {
            onError(new ProxyCacheException("Error closing socket input stream", e));
        }
    }

    private void closeSocketOutput(Socket socket) {
        try {
            if (!socket.isOutputShutdown()) {
                socket.shutdownOutput();
            }
        } catch (IOException e) {
            Log.d("leilu", "Failed to close socket on proxy side: {}. It seems client have already closed connection.", e);
        }
    }

    private void closeSocket(Socket socket) {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            onError(new ProxyCacheException("Error closing socket", e));
        }
    }


    private void onError(Throwable e) {
        Log.i("leilu", "HttpProxyCacheServer error", e);
    }


    private final class WaitRequestsRunnable
            implements Runnable {
        private final CountDownLatch startSignal;

        public WaitRequestsRunnable(CountDownLatch startSignal) {
            this.startSignal = startSignal;
        }


        public void run() {
            this.startSignal.countDown();
            HttpProxyCacheServer.this.waitForRequest();
        }
    }

    private final class SocketProcessorRunnable
            implements Runnable {
        private final Socket socket;

        public SocketProcessorRunnable(Socket socket) {
            this.socket = socket;
        }


        public void run() {
            HttpProxyCacheServer.this.processSocket(this.socket);
        }
    }


    public static final class Builder {
        private static final long DEFAULT_MAX_SIZE = 536870912L;

        private File cacheRoot;

        private FileNameGenerator fileNameGenerator;

        private DiskUsage diskUsage;

        private SourceInfoStorage sourceInfoStorage;
        private HeaderInjector headerInjector;

        public Builder(Context context) {
            this.sourceInfoStorage = SourceInfoStorageFactory.newSourceInfoStorage(context);
            this.cacheRoot = StorageUtils.getIndividualCacheDirectory(context);
            this.diskUsage = (DiskUsage) new TotalSizeLruDiskUsage(536870912L);
            this.fileNameGenerator = (FileNameGenerator) new Md5FileNameGenerator();
            this.headerInjector = (HeaderInjector) new EmptyHeadersInjector();
        }


        public Builder cacheDirectory(File file) {
            this.cacheRoot = Preconditions.checkNotNull(file);
            return this;
        }


        public Builder fileNameGenerator(FileNameGenerator fileNameGenerator) {
            this.fileNameGenerator = Preconditions.checkNotNull(fileNameGenerator);
            return this;
        }


        public Builder maxCacheSize(long maxSize) {
            this.diskUsage = (DiskUsage) new TotalSizeLruDiskUsage(maxSize);
            return this;
        }


        public Builder maxCacheFilesCount(int count) {
            this.diskUsage = (DiskUsage) new TotalCountLruDiskUsage(count);
            return this;
        }


        public Builder diskUsage(DiskUsage diskUsage) {
            this.diskUsage = Preconditions.checkNotNull(diskUsage);
            return this;
        }


        public Builder headerInjector(HeaderInjector headerInjector) {
            this.headerInjector = Preconditions.checkNotNull(headerInjector);
            return this;
        }


        public HttpProxyCacheServer build() {
            Config config = buildConfig();
            return new HttpProxyCacheServer(config);
        }


        private Config buildConfig() {
            return new Config(this.cacheRoot, this.fileNameGenerator, this.diskUsage, this.sourceInfoStorage, this.headerInjector);
        }
    }
}

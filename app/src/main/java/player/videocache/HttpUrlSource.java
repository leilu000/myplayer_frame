package player.videocache;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import player.videocache.headers.EmptyHeadersInjector;
import player.videocache.headers.HeaderInjector;
import player.videocache.sourcestorage.SourceInfoStorage;
import player.videocache.sourcestorage.SourceInfoStorageFactory;


public class HttpUrlSource
        implements Source {

    private static final int MAX_REDIRECTS = 5;
    private final SourceInfoStorage sourceInfoStorage;

    private final HeaderInjector headerInjector;
    private SourceInfo sourceInfo;
    private HttpURLConnection connection;
    private InputStream inputStream;

    public HttpUrlSource(String url) {
        this(url, SourceInfoStorageFactory.newEmptySourceInfoStorage());
    }


    public HttpUrlSource(String url, SourceInfoStorage sourceInfoStorage) {
        this(url, sourceInfoStorage, (HeaderInjector) new EmptyHeadersInjector());
    }


    public HttpUrlSource(String url, SourceInfoStorage sourceInfoStorage, HeaderInjector headerInjector) {
        this.sourceInfoStorage = Preconditions.checkNotNull(sourceInfoStorage);
        this.headerInjector = Preconditions.checkNotNull(headerInjector);
        SourceInfo sourceInfo = sourceInfoStorage.get(url);
        this.sourceInfo = (sourceInfo != null) ? sourceInfo : new SourceInfo(url, -2147483648L
                , ProxyCacheUtils.getSupposablyMime(url));
    }

    public HttpUrlSource(HttpUrlSource source) {
        this.sourceInfo = source.sourceInfo;
        this.sourceInfoStorage = source.sourceInfoStorage;
        this.headerInjector = source.headerInjector;
    }


    public synchronized long length() throws ProxyCacheException {
        if (this.sourceInfo.length == -2147483648L) {
            fetchContentInfo();
        }
        return this.sourceInfo.length;
    }


    public void open(long offset) throws ProxyCacheException {
        try {
            this.connection = openConnection(offset, -1);
            String mime = this.connection.getContentType();
            this.inputStream = new BufferedInputStream(this.connection.getInputStream(), 8192);
            long length = readSourceAvailableBytes(this.connection, offset, this.connection.getResponseCode());
            this.sourceInfo = new SourceInfo(this.sourceInfo.url, length, mime);
            this.sourceInfoStorage.put(this.sourceInfo.url, this.sourceInfo);
        } catch (IOException e) {
            throw new ProxyCacheException("Error opening connection for " + this.sourceInfo.url + " with offset " + offset, e);
        }
    }

    private long readSourceAvailableBytes(HttpURLConnection connection, long offset, int responseCode) throws IOException {
        long contentLength = getContentLength(connection);
        return (responseCode == 200) ? contentLength : ((responseCode == 206) ? (contentLength + offset) : this.sourceInfo.length);
    }


    private long getContentLength(HttpURLConnection connection) {
        String contentLengthValue = connection.getHeaderField("Content-Length");
        return (contentLengthValue == null) ? -1L : Long.parseLong(contentLengthValue);
    }


    public void close() throws ProxyCacheException {
        if (this.connection != null) {
            try {
                this.connection.disconnect();
            } catch (NullPointerException | IllegalArgumentException e) {
                String message = "Wait... but why? WTF!? Really shouldn't happen any more after fixing https://github.com/danikula/AndroidVideoCache/issues/43. If you read it on your device log, please, notify me danikula@gmail.com or create issue here https://github.com/danikula/AndroidVideoCache/issues.";


                throw new RuntimeException(message, e);
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e("leilu", " closing connection correctly. Should happen only on Android L. If anybody know how to fix it, please visit https://github.com/danikula/AndroidVideoCache/issues/88. Until good solution is not know, just ignore this issue :(", e);
            }
        }
    }


    public int read(byte[] buffer) throws ProxyCacheException {
        if (this.inputStream == null) {
            throw new ProxyCacheException("Error reading data from " + this.sourceInfo.url + ": connection is absent!");
        }
        try {
            return this.inputStream.read(buffer, 0, buffer.length);
        } catch (InterruptedIOException e) {
            throw new InterruptedProxyCacheException("Reading source " + this.sourceInfo.url + " is interrupted", e);
        } catch (IOException e) {
            throw new ProxyCacheException("Error reading data from " + this.sourceInfo.url, e);
        }
    }

    private void fetchContentInfo() throws ProxyCacheException {
        Log.d("leilu", "Read content info from " + this.sourceInfo.url);
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = openConnection(0L, 10000);
            long length = getContentLength(urlConnection);
            String mime = urlConnection.getContentType();
            inputStream = urlConnection.getInputStream();
            this.sourceInfo = new SourceInfo(this.sourceInfo.url, length, mime);
            this.sourceInfoStorage.put(this.sourceInfo.url, this.sourceInfo);
            Log.d("leilu", "Source info fetched: " + this.sourceInfo);
        } catch (IOException e) {
            Log.d("leilu", "Error fetching info from " + this.sourceInfo.url, e);
        } finally {
            ProxyCacheUtils.close(inputStream);
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private HttpURLConnection openConnection(long offset, int timeout) throws IOException, ProxyCacheException {
        boolean redirected;
        HttpURLConnection connection;
        int redirectCount = 0;
        String url = this.sourceInfo.url;
        do {
            Log.d("leilu", "VideoCache-->Open connection " + ((offset > 0L) ? (" with offset " + offset) : "") + " to " + url);
            connection = (HttpURLConnection) (new URL(url)).openConnection();
            injectCustomHeaders(connection, url);
            if (offset > 0L) {
                connection.setRequestProperty("Range", "bytes=" + offset + "-");
            }
            if (timeout > 0) {
                connection.setConnectTimeout(timeout);
                connection.setReadTimeout(timeout);
            }
            int code = connection.getResponseCode();
            redirected = (code == 301 || code == 302 || code == 303);
            if (redirected) {
                url = connection.getHeaderField("Location");
                redirectCount++;
                connection.disconnect();
            }
            if (redirectCount > 5) {
                throw new ProxyCacheException("Too many redirects: " + redirectCount);
            }
        } while (redirected);
        return connection;
    }

    private void injectCustomHeaders(HttpURLConnection connection, String url) {
        Map<String, String> extraHeaders = this.headerInjector.addHeaders(url);
        for (Map.Entry<String, String> header : extraHeaders.entrySet()) {
            connection.setRequestProperty(header.getKey(), header.getValue());
        }
    }

    public synchronized String getMime() throws ProxyCacheException {
        if (TextUtils.isEmpty(this.sourceInfo.mime)) {
            fetchContentInfo();
        }
        return this.sourceInfo.mime;
    }


    public String getUrl() {
        return this.sourceInfo.url;
    }


    public String toString() {
        return "HttpUrlSource{sourceInfo='" + this.sourceInfo + "}";
    }
}

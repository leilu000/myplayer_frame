package player.videocache;

import android.text.TextUtils;
import android.util.Log;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Locale;

import player.videocache.file.FileCache;


class HttpProxyCache
        extends ProxyCache {
    private static final float NO_CACHE_BARRIER = 0.2F;
    private final HttpUrlSource source;
    private final FileCache cache;
    private CacheListener listener;

    public HttpProxyCache(HttpUrlSource source, FileCache cache) {
        super(source, (Cache) cache);
        this.cache = cache;
        this.source = source;
    }


    public void registerCacheListener(CacheListener cacheListener) {
        this.listener = cacheListener;
    }


    public void processRequest(GetRequest request, Socket socket) throws IOException, ProxyCacheException {
        OutputStream out = new BufferedOutputStream(socket.getOutputStream());
        String responseHeaders = newResponseHeaders(request);
        out.write(responseHeaders.getBytes("UTF-8"));

        long offset = request.rangeOffset;
        Log.i("leilu", "VideoCache-->processRequest,responseHeaders:" + responseHeaders + "  \noffset:" + offset);
        if (isUseCache(request)) {
            responseWithCache(out, offset);
        } else {
            responseWithoutCache(out, offset);
        }
    }

    private boolean isUseCache(GetRequest request) throws ProxyCacheException {
        long sourceLength = this.source.length();
        boolean sourceLengthKnown = (sourceLength > 0L);
        long cacheAvailable = this.cache.available();

        return (!sourceLengthKnown || !request.partial || (float) request.rangeOffset <= (float) cacheAvailable + (float) sourceLength * 0.2F);
    }

    private String newResponseHeaders(GetRequest request) throws IOException, ProxyCacheException {
        String mime = this.source.getMime();
        boolean mimeKnown = !TextUtils.isEmpty(mime);
        long length = this.cache.isCompleted() ? this.cache.available() : this.source.length();
        boolean lengthKnown = (length >= 0L);
        long contentLength = request.partial ? (length - request.rangeOffset) : length;
        boolean addRange = (lengthKnown && request.partial);
        return (request.partial ? "HTTP/1.1 206 PARTIAL CONTENT\n" : "HTTP/1.1 200 OK\n") +
                "Accept-Ranges: bytes\n" + (
                lengthKnown ?
                        format("Content-Length: %d\n", new Object[]{Long.valueOf(contentLength)}) : "") + (addRange ?
                format("Content-Range: bytes %d-%d/%d\n", new Object[]{Long.valueOf(request.rangeOffset), Long.valueOf(length - 1L), Long.valueOf(length)}) : "") + (mimeKnown ?
                format("Content-Type: %s\n", new Object[]{mime}) : "") + "\n";
    }


    private void responseWithCache(OutputStream out, long offset) throws ProxyCacheException, IOException {
        Log.i("leilu", "VideoCache-->responseWithCache,url:" + source.getUrl() + "  offset:" + offset);
        byte[] buffer = new byte[8192];
        int readBytes;
        while ((readBytes = read(buffer, offset, buffer.length)) != -1) {
            out.write(buffer, 0, readBytes);
            offset += readBytes;
        }
        out.flush();
    }

    private void responseWithoutCache(OutputStream out, long offset) throws ProxyCacheException, IOException {
        Log.i("leilu", "VideoCache-->responseWithoutCache,url:" + source.getUrl() + "  offset:" + offset);
        HttpUrlSource newSourceNoCache = new HttpUrlSource(this.source);
        try {
            newSourceNoCache.open((int) offset);
            byte[] buffer = new byte[8192];
            int readBytes;
            while ((readBytes = newSourceNoCache.read(buffer)) != -1) {
                out.write(buffer, 0, readBytes);
                offset += readBytes;
            }
            out.flush();
        } finally {
            newSourceNoCache.close();
        }
    }


    private String format(String pattern, Object... args) {
        return String.format(Locale.US, pattern, args);
    }


    protected void onCachePercentsAvailableChanged(int percents) {
        if (this.listener != null)
            this.listener.onCacheAvailable(this.cache.file, this.source.getUrl(), percents);
    }
}

package player.manager;


import com.leilu.playerframe.BaseApplication;

import player.videocache.HttpProxyCacheServer;

/**
 * Created by ll on 2019/12/22.
 */
public class HttpProxyCacheUtil {

    private static HttpProxyCacheUtil mHttpProxyCacheUtil = new HttpProxyCacheUtil();
    private HttpProxyCacheServer mHttpProxyCacheServer;

    private HttpProxyCacheUtil() {

    }

    public synchronized void init(HttpProxyCacheServer.Builder builder) {
        if (mHttpProxyCacheServer == null) {
            HttpProxyCacheServer.Builder b = builder;
            if (b == null) {
                b = new HttpProxyCacheServer.Builder(BaseApplication.getContext());
            }
            mHttpProxyCacheServer = b.build();
        }
    }

    public static HttpProxyCacheUtil getInstance() {
        return mHttpProxyCacheUtil;
    }

    public synchronized HttpProxyCacheServer getCacheServer() {
        return mHttpProxyCacheServer;
    }


}

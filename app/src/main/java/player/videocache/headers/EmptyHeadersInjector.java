package player.videocache.headers;

import java.util.HashMap;
import java.util.Map;


public class EmptyHeadersInjector
        implements HeaderInjector {
    public Map<String, String> addHeaders(String url) {
        return new HashMap<>();
    }
}

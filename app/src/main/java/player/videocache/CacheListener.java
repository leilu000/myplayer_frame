package player.videocache;

import java.io.File;

public interface CacheListener {
    void onCacheAvailable(File paramFile, String paramString, int paramInt);
}

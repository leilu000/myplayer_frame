package player.videocache.sourcestorage;


import player.videocache.SourceInfo;

public interface SourceInfoStorage {
    SourceInfo get(String paramString);

    void put(String paramString, SourceInfo paramSourceInfo);

    void release();
}

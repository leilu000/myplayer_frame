package player.videocache.sourcestorage;


import player.videocache.SourceInfo;

public class NoSourceInfoStorage implements SourceInfoStorage {
    public SourceInfo get(String url) {
        return null;
    }

    public void put(String url, SourceInfo sourceInfo) {
    }

    public void release() {
    }
}

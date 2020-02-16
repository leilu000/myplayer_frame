package player.videocache;



import java.io.File;

import player.videocache.file.DiskUsage;
import player.videocache.file.FileNameGenerator;
import player.videocache.headers.HeaderInjector;
import player.videocache.sourcestorage.SourceInfoStorage;


class Config {
    public final File cacheRoot;
    public final FileNameGenerator fileNameGenerator;
    public final DiskUsage diskUsage;
    public final SourceInfoStorage sourceInfoStorage;
    public final HeaderInjector headerInjector;

    Config(File cacheRoot, FileNameGenerator fileNameGenerator, DiskUsage diskUsage, SourceInfoStorage sourceInfoStorage, HeaderInjector headerInjector) {
        this.cacheRoot = cacheRoot;
        this.fileNameGenerator = fileNameGenerator;
        this.diskUsage = diskUsage;
        this.sourceInfoStorage = sourceInfoStorage;
        this.headerInjector = headerInjector;
    }

    File generateCacheFile(String url) {
        String name = this.fileNameGenerator.generate(url);
        return new File(this.cacheRoot, name);
    }
}

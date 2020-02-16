package player.videocache.file;


import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public abstract class LruDiskUsage
        implements DiskUsage {
    private final ExecutorService workerThread = Executors.newSingleThreadExecutor();


    public void touch(File file) throws IOException {
        this.workerThread.submit(new TouchCallable(file));
    }


    private void touchInBackground(File file) throws IOException {
        Files.setLastModifiedNow(file);
        List<File> files = Files.getLruListFiles(file.getParentFile());
        trim(files);
    }


    private void trim(List<File> files) {
        long totalSize = countTotalSize(files);
        int totalCount = files.size();
        for (File file : files) {
            boolean accepted = accept(file, totalSize, totalCount);
            if (!accepted) {
                long fileSize = file.length();
                boolean deleted = file.delete();
                if (deleted) {
                    totalCount--;
                    totalSize -= fileSize;
                    Log.i("leilu", "Cache file " + file + " is deleted because it exceeds cache limit");
                    continue;
                }
                Log.i("leilu", "Error deleting file " + file + " for trimming cache");
            }
        }
    }


    private long countTotalSize(List<File> files) {
        long totalSize = 0L;
        for (File file : files) {
            totalSize += file.length();
        }
        return totalSize;
    }

    protected abstract boolean accept(File paramFile, long paramLong, int paramInt);

    private class TouchCallable implements Callable<Void> {
        private final File file;

        public TouchCallable(File file) {
            this.file = file;
        }


        public Void call() throws Exception {
            LruDiskUsage.this.touchInBackground(this.file);
            return null;
        }
    }
}

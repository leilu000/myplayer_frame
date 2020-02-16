package player.videocache.file;



import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import player.videocache.Cache;
import player.videocache.ProxyCacheException;


public class FileCache implements Cache {
    private static final String TEMP_POSTFIX = ".download";
    private final DiskUsage diskUsage;
    public File file;
    private RandomAccessFile dataFile;

    public FileCache(File file) throws ProxyCacheException {
        this(file, new UnlimitedDiskUsage());
    }


    public FileCache(File file, DiskUsage diskUsage) throws ProxyCacheException {
        try {
            if (diskUsage == null) {
                throw new NullPointerException();
            }
            this.diskUsage = diskUsage;
            File directory = file.getParentFile();
            Files.makeDir(directory);
            boolean completed = file.exists();
            this.file = completed ? file : new File(file.getParentFile(), file.getName() + ".download");
            this.dataFile = new RandomAccessFile(this.file, completed ? "r" : "rw");
        } catch (IOException e) {
            throw new ProxyCacheException("Error using file " + file + " as disc cache", e);
        }
    }


    public synchronized long available() throws ProxyCacheException {
        try {
            return (int) this.dataFile.length();
        } catch (IOException e) {
            throw new ProxyCacheException("Error reading length of file " + this.file, e);
        }
    }


    public synchronized int read(byte[] buffer, long offset, int length) throws ProxyCacheException {
        try {
            this.dataFile.seek(offset);
            return this.dataFile.read(buffer, 0, length);
        } catch (IOException e) {
            String format = "Error reading %d bytes with offset %d from file[%d bytes] to buffer[%d bytes]";
            throw new ProxyCacheException(String.format(format, new Object[]{Integer.valueOf(length), Long.valueOf(offset), Long.valueOf(available()), Integer.valueOf(buffer.length)}), e);
        }
    }


    public synchronized void append(byte[] data, int length) throws ProxyCacheException {
        try {
            if (isCompleted()) {
                throw new ProxyCacheException("Error append cache: cache file " + this.file + " is completed!");
            }
            this.dataFile.seek(available());
            this.dataFile.write(data, 0, length);
        } catch (IOException e) {
            String format = "Error writing %d bytes to %s from buffer with size %d";
            throw new ProxyCacheException(String.format(format, new Object[]{Integer.valueOf(length), this.dataFile, Integer.valueOf(data.length)}), e);
        }
    }


    public synchronized void close() throws ProxyCacheException {
        try {
            this.dataFile.close();
            this.diskUsage.touch(this.file);
        } catch (IOException e) {
            throw new ProxyCacheException("Error closing file " + this.file, e);
        }
    }


    public synchronized void complete() throws ProxyCacheException {
        if (isCompleted()) {
            return;
        }

        close();
        String fileName = this.file.getName().substring(0, this.file.getName().length() - ".download".length());
        File completedFile = new File(this.file.getParentFile(), fileName);
        boolean renamed = this.file.renameTo(completedFile);
        if (!renamed) {
            throw new ProxyCacheException("Error renaming file " + this.file + " to " + completedFile + " for completion!");
        }
        this.file = completedFile;
        try {
            this.dataFile = new RandomAccessFile(this.file, "r");
            this.diskUsage.touch(this.file);
        } catch (IOException e) {
            throw new ProxyCacheException("Error opening " + this.file + " as disc cache", e);
        }
    }


    public synchronized boolean isCompleted() {
        return !isTempFile(this.file);
    }


    public File getFile() {
        return this.file;
    }


    private boolean isTempFile(File file) {
        return file.getName().endsWith(".download");
    }
}

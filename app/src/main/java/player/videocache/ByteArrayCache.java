package player.videocache;

import java.io.ByteArrayInputStream;
import java.util.Arrays;


public class ByteArrayCache
        implements Cache {
    private volatile byte[] data;
    private volatile boolean completed;

    public ByteArrayCache() {
        this(new byte[0]);
    }


    public ByteArrayCache(byte[] data) {
        this.data = Preconditions.checkNotNull(data);
    }


    public int read(byte[] buffer, long offset, int length) throws ProxyCacheException {
        if (offset >= this.data.length) {
            return -1;
        }
        if (offset > 2147483647L) {
            throw new IllegalArgumentException("Too long offset for memory cache " + offset);
        }
        return (new ByteArrayInputStream(this.data)).read(buffer, (int) offset, length);
    }


    public long available() throws ProxyCacheException {
        return this.data.length;
    }


    public void append(byte[] newData, int length) throws ProxyCacheException {
        Preconditions.checkNotNull(this.data);
        Preconditions.checkArgument((length >= 0 && length <= newData.length));

        byte[] appendedData = Arrays.copyOf(this.data, this.data.length + length);
        System.arraycopy(newData, 0, appendedData, this.data.length, length);
        this.data = appendedData;
    }


    public void close() throws ProxyCacheException {
    }


    public void complete() {
        this.completed = true;
    }


    public boolean isCompleted() {
        return this.completed;
    }
}

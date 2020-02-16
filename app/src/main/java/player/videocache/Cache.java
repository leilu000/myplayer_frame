package player.videocache;

public interface Cache {
    long available() throws ProxyCacheException;

    int read(byte[] paramArrayOfbyte, long paramLong, int paramInt) throws ProxyCacheException;

    void append(byte[] paramArrayOfbyte, int paramInt) throws ProxyCacheException;

    void close() throws ProxyCacheException;

    void complete() throws ProxyCacheException;

    boolean isCompleted();
}

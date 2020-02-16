package player.videocache;

public interface Source {
  void open(long paramLong) throws ProxyCacheException;
  
  long length() throws ProxyCacheException;
  
  int read(byte[] paramArrayOfbyte) throws ProxyCacheException;
  
  void close() throws ProxyCacheException;
}

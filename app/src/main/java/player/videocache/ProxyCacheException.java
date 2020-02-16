package player.videocache;


public class ProxyCacheException
        extends Exception {
    private static final String LIBRARY_VERSION = ". Version: 2.7.1";

    public ProxyCacheException(String message) {
        super(message + ". Version: 2.7.1");
    }


    public ProxyCacheException(String message, Throwable cause) {
        super(message + ". Version: 2.7.1", cause);
    }


    public ProxyCacheException(Throwable cause) {
        super("No explanation error. Version: 2.7.1", cause);
    }
}

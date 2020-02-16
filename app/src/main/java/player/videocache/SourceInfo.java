package player.videocache;


public class SourceInfo {
    public final String url;
    public final long length;
    public final String mime;

    public SourceInfo(String url, long length, String mime) {
        this.url = url;
        this.length = length;
        this.mime = mime;
    }


    public String toString() {
        return "SourceInfo{url='" + this.url + '\'' + ", length=" + this.length + ", mime='" + this.mime + '\'' + '}';
    }
}

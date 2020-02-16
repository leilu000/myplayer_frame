package player.videocache.file;

import android.text.TextUtils;

import player.videocache.ProxyCacheUtils;


public class Md5FileNameGenerator
        implements FileNameGenerator {
    private static final int MAX_EXTENSION_LENGTH = 4;

    public String generate(String url) {
        String extension = getExtension(url);
        String name = ProxyCacheUtils.computeMD5(url);
        return TextUtils.isEmpty(extension) ? name : (name + "." + extension);
    }

    private String getExtension(String url) {
        int dotIndex = url.lastIndexOf('.');
        int slashIndex = url.lastIndexOf('/');
        return (dotIndex != -1 && dotIndex > slashIndex && dotIndex + 2 + 4 > url.length()) ? url
                .substring(dotIndex + 1, url.length()) : "";
    }
}

package player.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.leilu.playerframe.BaseApplication;

/**
 * 配置类
 * Created by ll on 2019/12/5.
 */
public class Config {

    private static volatile Config sConfig;

    private static final String NAME = "yome_player_config";
    private static final String KEY_X = "x_position";
    private static final String KEY_Y = "y_position";

    private Config() {
    }

    public static Config getInstance() {
        if (sConfig == null) {
            synchronized (Config.class) {
                if (sConfig == null) {
                    sConfig = new Config();
                }
            }
        }
        return sConfig;
    }

    private SharedPreferences getSharedPreferences() {
        return BaseApplication.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public float[] getTinyWindowPosition() {
        float x = getSharedPreferences().getFloat(KEY_X, 0);
        float y = getSharedPreferences().getFloat(KEY_Y, 0);
        return new float[]{x, y};
    }

    public void saveTinyWindowPosition(float x, float y) {
        Log.i("==", "saveTinyWindowPosition    x:" + x + "   y:" + y);
        getSharedPreferences().edit().putFloat(KEY_X, x).putFloat(KEY_Y, y).apply();
    }
}

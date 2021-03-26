package player.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import com.leilu.playerframe.BaseApplication;

public class Utils {
    private static DisplayMetrics sDisplayMetrics;
    private static final float DOT_FIVE = 0.5f;



    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = BaseApplication.getContext().getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = BaseApplication.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * init display metrics
     *
     * @param context
     */
    private static synchronized void initDisplayMetrics(Context context) {
        sDisplayMetrics = context.getResources().getDisplayMetrics();
    }

    /**
     * @return
     */
    public static float getDensity() {
        initDisplayMetrics(BaseApplication.getContext());
        return sDisplayMetrics.density;
    }

    /**
     * @param dip
     * @return
     */
    public static int dip2px(float dip) {
        float density = getDensity();
        return (int) (dip * density + DOT_FIVE);
    }

    /**
     * 获得屏幕宽度
     * * @return
     */
    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) BaseApplication.getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        WindowManager wm = (WindowManager) BaseApplication.getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获得当前屏幕亮度值 0--255
     */
    public static int getScreenBrightness() {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(BaseApplication.getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception localException) {
        }
        return screenBrightness;
    }

    /**
     * 设置当前Activity屏幕亮度
     *
     * @param paramInt 0~255
     * @param activity
     */
    public static void setScreenBrightness(int paramInt, Activity activity) {
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        float f = paramInt / 255.0F;
        localLayoutParams.screenBrightness = f;
        localWindow.setAttributes(localLayoutParams);
    }


    /**
     * 获取最大播放音量
     *
     * @return
     */
    public static int getStreamMaxVolume() {
        AudioManager audioManager = (AudioManager) BaseApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    /**
     * 获取当前播放音量
     *
     * @return
     */
    public static int getStreamVolume() {
        AudioManager audioManager = (AudioManager) BaseApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    /**
     * 设置音量
     *
     * @param value 0~getStreamMaxVolume
     * @param flag  AudioManager.FLAG_SHOW_UI等等
     */
    public static void setStreamVolume(int value, int flag) {
        AudioManager audioManager = (AudioManager) BaseApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, flag);
        }
    }


    public static Activity getActivityForContext(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return getActivityForContext(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    public static int getChildIndexOfViewGroup(ViewGroup parent, View childView) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (parent.getChildAt(i) == childView) {
                return i;
            }
        }
        return -1;
    }

    @SuppressLint("RestrictedApi")
    public static void showActionBar(Context context) {
        ActionBar ab = getAppCompActivity(context).getSupportActionBar();
        if (ab != null) {
            ab.setShowHideAnimationEnabled(false);
            ab.show();
        }
        getActivityForContext(context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * @param context
     */
    @SuppressLint("RestrictedApi")
    public static void hideActionBar(Context context) {
        ActionBar ab = getAppCompActivity(context).getSupportActionBar();
        if (ab != null) {
            ab.setShowHideAnimationEnabled(false);
            ab.hide();
        }
        getActivityForContext(context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    private static AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }


}

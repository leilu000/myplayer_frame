package player.base;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.leilu.playerframe.R;

import java.util.Timer;
import java.util.TimerTask;

import player.bean.DisplayMode;
import player.bean.PlayerState;
import player.bean.SimplePlayerListener;
import player.manager.NikoPlayer;
import player.util.Utils;

/**
 * Created by ll on 2019/12/5.
 */
public abstract class BasePlayerControlView extends FrameLayout implements View.OnTouchListener {

    private static final int DIRECTION_VERTICAL = 0;
    private static final int DIRECTION_HORIZONTAL = 1;
    private static final int DIRECTION_UNKNOW = -1;

    public static final int MAX_VALUE_SCREEN_BRIGHTNESS = 255;
    public static final int MAX_VALUE_STREAM_VOLUME = Utils.getStreamMaxVolume();

    protected View mRootView;

    private CountDownTimer mProgressTimer;
    private CountDownTimer mHideTimer;
    private PlayerListener mPlayerListener;
    protected NikoPlayer mNikoPlayer;
    private int mScaledTouchSlop;
    private float mCurrentVolume = 1.0f;
    private int mCurrentBrightness;
    private boolean mIsShowing;
    private boolean mIsVolumeChangeStart;
    private boolean mIsBrightnessChangeStart;

    public BasePlayerControlView(@NonNull Context context) {
        this(context, null);
    }

    public BasePlayerControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasePlayerControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Color.TRANSPARENT);
        LayoutInflater.from(getContext()).inflate(getControlViewLayoutId(), this, true);
        mRootView = getChildAt(0);
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mCurrentBrightness = Utils.getScreenBrightness();
        initAttrs(attrs);
        initView();
        setOnTouchListener(this);
        hide();
    }

    protected void show(boolean isStartHideTimer) {
        cancelHideTimer();
        if (!mIsShowing) {
            mIsShowing = true;
            startProgressTimer();
            if (isStartHideTimer) {
                startHideTimer();
            }
            showControllerView();
        }
    }

    protected abstract void showControllerView();

    protected abstract void hideControllerView();

    protected void hide() {
        if (mIsShowing) {
            mIsShowing = false;
            cancelProgressTimer();
            hideControllerView();
        }
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.PlayerControllerView);
        mIsShowing = ta.getBoolean(R.styleable.PlayerControllerView_is_showing, true);
        ta.recycle();
    }


    @UiThread
    public void attachPlayerMgr(NikoPlayer mgr) {
        if (mgr != null && mNikoPlayer == null) {
            release();
            mNikoPlayer = mgr;
            mPlayerListener = new PlayerListener();
            mNikoPlayer.addPlayerListener(mPlayerListener);
            initListener();
        }
    }

    @UiThread
    public void detachPlayerMgr() {
        release();
    }

    protected abstract void initListener();

    protected abstract void initView();

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
    }

    protected void release() {
        cancelProgressTimer();
        cancelHideTimer();
        clearListener();
        mNikoPlayer = null;
    }


    private void cancelProgressTimer() {
        if (mProgressTimer != null) {
            mProgressTimer.cancel();
            mProgressTimer = null;
        }
    }

    private void cancelHideTimer() {
        if (mHideTimer != null) {
            mHideTimer.cancel();
            mHideTimer = null;
        }
    }

    private void startProgressTimer() {
        cancelProgressTimer();
        if (getVisibility() == View.VISIBLE && isNeedStartProgressTimer()) {
            mProgressTimer = new CountDownTimer(1000, Integer.MAX_VALUE) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    onProgressChanged(mNikoPlayer.getDuration(), mNikoPlayer.getProgress());
                }
            };
            mProgressTimer.start();
        }
    }

    private void startHideTimer() {
        if (!mIsShowing) {
            return;
        }
        cancelHideTimer();
        if (getVisibility() == View.VISIBLE && isNeedStartProgressTimer()) {
            mHideTimer = new CountDownTimer(2000, 1) {

                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    hide();
                }
            };
            mHideTimer.start();
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        hide();
        startProgressTimer();
    }

    private void clearListener() {
        if (mPlayerListener != null && mNikoPlayer != null) {
            mNikoPlayer.removePlayerListener(mPlayerListener);
            mPlayerListener = null;
        }
    }


    protected void seek(long timeInUs) {
        if (mNikoPlayer != null) {
            mNikoPlayer.seek(timeInUs);
        }
    }

    protected void pause() {
        if (mNikoPlayer != null) {
            mNikoPlayer.pause();
        }
    }

    protected void play() {
        if (mNikoPlayer != null) {
            mNikoPlayer.start();
        }
    }

    protected void togglePlay() {
        if (getPlayerState() == PlayerState.PLAYING) {
            pause();
        } else {
            play();
        }
    }

    protected void stop() {
        if (mNikoPlayer != null) {
            mNikoPlayer.stop();
        }
    }

    protected void setVolume(float left, float right) {
        if (mNikoPlayer != null) {
            mNikoPlayer.setVolume(left, right);
        }
    }

    protected long getDuration() {
        if (mNikoPlayer != null) {
            return mNikoPlayer.getDuration();
        }
        return 0;
    }

    protected long getCurrentProgress() {
        if (mNikoPlayer != null) {
            return mNikoPlayer.getProgress();
        }
        return 0;
    }

    protected abstract int getControlViewLayoutId();

    /**
     * 是否定时获取当前播放进度
     *
     * @return
     */
    protected abstract boolean isNeedStartProgressTimer();

    /**
     * 当播放进度发生改变的回调
     * 如果是手势拖动，则发生改变就回调
     * 如果是定时器,500ms回调一次,需要isNeedStartProgressTimer返回true才能生效
     *
     * @param duration
     * @param progress
     */
    protected abstract void onProgressChanged(long duration, long progress);

    protected abstract void onError(String msg);

    /**
     * 播放完成
     */
    protected abstract void onComplete();

    protected abstract void onPaused();

    protected abstract void onBufferingEnd();

    protected abstract void onBufferingStart();

    protected abstract void onPlaying(long duration, long progress);

    protected abstract void onDisplayModeChanged(DisplayMode orientation);

    private class PlayerListener extends SimplePlayerListener {

        @Override
        public void onDisplayModeChanged(DisplayMode orientation) {
            BasePlayerControlView.this.onDisplayModeChanged(orientation);
        }

        @Override
        public void onPaused() {
            BasePlayerControlView.this.onPaused();
        }

        @Override
        public void onBufferingStart() {
            BasePlayerControlView.this.onBufferingStart();
        }

        @Override
        public void onBufferingEnd() {
            BasePlayerControlView.this.onBufferingEnd();
        }

        @Override
        public void onError(String msg, int type) {
            cancelProgressTimer();
            BasePlayerControlView.this.onError(msg);
        }

        @Override
        public void onComplete() {
            cancelProgressTimer();
            onProgressChanged(getDuration(), getDuration());
            BasePlayerControlView.this.onComplete();
        }

        @Override
        public void onPlaying() {
            startProgressTimer();
            startHideTimer();
            BasePlayerControlView.this.onPlaying(mNikoPlayer.getDuration(), mNikoPlayer.getProgress());
        }
    }

    private float mStartX, mStartY;
    private int mScrollDirection = DIRECTION_UNKNOW;
    private long mHorizontalProgress;
    private float mDY;
    private float mDX;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mNikoPlayer == null) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                mScrollDirection = DIRECTION_UNKNOW;
                mStartY = event.getY();
                mStartX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                doActionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                mCurrentBrightness = getScreenBrightness();
                mCurrentVolume = Utils.getStreamVolume();
                if (mScrollDirection == DIRECTION_HORIZONTAL) {
                    seek(mHorizontalProgress);
                    play();
                    startProgressTimer();
                    onSeekEnd();
                }
                if (mIsBrightnessChangeStart) {
                    mIsBrightnessChangeStart = false;
                    onBrightnessChangeEnd();
                }
                if (mIsVolumeChangeStart) {
                    mIsVolumeChangeStart = false;
                    onVolumeChangeEnd();
                }
                mScrollDirection = DIRECTION_UNKNOW;
                if (mIsShowing) {
                    hide();
                } else {
                    show(true);
                }
                break;
        }
        return true;
    }

    private void doActionMove(MotionEvent event) {
        if (!isSupportSeek()) {
            Log.w("leilu", "The isSupportSeek is false !");
            return;
        }
        mDY = event.getY() - mStartY;
        mDX = event.getX() - mStartX;
        if (isVertical(mDX, mDY) && mScrollDirection != DIRECTION_HORIZONTAL) {
            mScrollDirection = DIRECTION_VERTICAL;
            if (mStartX <= getWidth() / 2) {
                doBrightness();
            } else {// 在右边
                doVolume();
            }
            show(false);
        } else if (isHorizontal(mDX, mDY) && mScrollDirection != DIRECTION_VERTICAL) {
            doProgress();
            show(false);
        }
    }

    private void doVolume() {
        if (!isSupportVolume()) {
            Log.w("leilu", "The isSupportVolume is false !");
            return;
        }
        if (!mIsVolumeChangeStart) {
            mIsVolumeChangeStart = true;
            onVolumeChangeStart();
        }
        float avg = (MAX_VALUE_STREAM_VOLUME) * 1.0f / getHeight();
        int value = (int) (mCurrentVolume - avg * mDY);
        if (value > MAX_VALUE_STREAM_VOLUME) {
            value = MAX_VALUE_STREAM_VOLUME;
        }
        if (value < 0) {
            value = 0;
        }
        Utils.setStreamVolume(value, AudioManager.FLAG_PLAY_SOUND);
        onVolumeChanged(value, MAX_VALUE_STREAM_VOLUME);
    }

    /**
     * 亮度改变开始
     */
    protected abstract void onBrightnessChangeStart();

    /**
     * 亮度改变结束
     */
    protected abstract void onBrightnessChangeEnd();

    /**
     * 音量改变开始
     */
    protected abstract void onVolumeChangeStart();

    /**
     * 音量改变结束
     */
    protected abstract void onVolumeChangeEnd();

    /**
     * 是否支持调整亮度
     *
     * @return
     */
    protected abstract boolean isSupportBrightness();

    /**
     * 是否支持调整音量
     *
     * @return
     */
    protected abstract boolean isSupportVolume();

    /**
     * 是否支持seek进度
     *
     * @return
     */
    protected abstract boolean isSupportSeek();

    private void doBrightness() {
        if (!isSupportBrightness()) {
            Log.w("leilu", "The isSupportBrightness is false !");
            return;
        }
        if (!mIsBrightnessChangeStart) {
            mIsBrightnessChangeStart = true;
            onBrightnessChangeStart();
        }
        float avg = (MAX_VALUE_SCREEN_BRIGHTNESS) * 1.0f / getHeight();
        int value = (int) (mCurrentBrightness - avg * mDY);
        if (value > MAX_VALUE_SCREEN_BRIGHTNESS) {
            value = MAX_VALUE_SCREEN_BRIGHTNESS;
        }
        if (value < 0) {
            value = 0;
        }
        Utils.setScreenBrightness(value, ((Activity) getContext()));
        onBrightnessChanged(value, MAX_VALUE_SCREEN_BRIGHTNESS);
    }

    protected abstract void onSeekStart();

    protected abstract void onSeekEnd();

    private void doProgress() {
        if (mScrollDirection != DIRECTION_HORIZONTAL) {
            mScrollDirection = DIRECTION_HORIZONTAL;
            onSeekStart();
        }
        cancelProgressTimer();
        long duration = getDuration();
        long avgProgress = duration / getWidth();
        long progress = (long) (getCurrentProgress() + mDX * avgProgress);
        if (progress < 0) {
            progress = 0;
        }
        if (progress > duration) {
            progress = duration;
        }
        mHorizontalProgress = progress;
        onProgressChanged(duration, progress);
    }


    private boolean isVertical(float dx, float dy) {
        return Math.abs(dx) < Math.abs(dy) && Math.abs(dy) > mScaledTouchSlop;
    }

    private boolean isHorizontal(float dx, float dy) {
        return Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > mScaledTouchSlop;
    }

    /**
     * 屏幕亮度改变
     *
     * @param currentBrightness 当前亮度
     * @param maxBrightness     最大的亮度
     */
    protected abstract void onBrightnessChanged(int currentBrightness, int maxBrightness);

    /**
     * 音量改变
     *
     * @param value
     */
    protected abstract void onVolumeChanged(float value, int maxVolume);

    protected void setDisplayMode(DisplayMode mode) {
        mNikoPlayer.setDisplayMode(mode);
    }

    protected boolean isLandscape() {
        return mNikoPlayer.getDisplayMode() == DisplayMode.LANDSCAPE_FULL_SCREEN;
    }

    protected PlayerState getPlayerState() {
        if (mNikoPlayer != null) {
            return mNikoPlayer.getState();
        }
        return PlayerState.IDLE;
    }

    protected int getScreenBrightness() {
        return (int) (((Activity) getContext()).getWindow().getAttributes().screenBrightness * 255f);
    }

    protected int getScreamVolume() {
        return Utils.getStreamVolume();
    }
}

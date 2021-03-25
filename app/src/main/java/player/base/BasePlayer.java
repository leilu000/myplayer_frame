package player.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;

import player.base.inter.IPlayer;
import player.base.inter.IPlayerListener;
import player.bean.DisplayMode;
import player.bean.PlayerParam;
import player.bean.PlayerState;


/**
 * 播放器的基类，任何播放器都应该继承此类进行扩展
 * Created by ll on 2019/12/6.
 */
public abstract class BasePlayer implements IPlayer {

    private static final int EVENT_RECONNECT = -10000;

    protected PlayerParam mPlayerParam;
    protected IPlayerListener mListener;
    private String mDataSource;
    private Surface mSurface;
    private long mTimeInMs;
    private int mBufferingPercent;
    private boolean mIsStartMethodInvoked;

    protected PlayerState mState = PlayerState.IDLE;
    private volatile boolean mIsWorking;

    private volatile boolean mIsInitialing;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case EVENT_RECONNECT:
                    // 错误重连
                    if (!mIsInitialing) {
                        Log.i("leilu", "BasePlayer-->reconnect");
                        mBufferingPercent = 0;
                        mIsInitialing = false;
                        mHandler.removeMessages(EVENT_RECONNECT);
                        init();
                    }
                    break;
            }
        }
    };

    public BasePlayer(PlayerParam playerParam) {
        mPlayerParam = playerParam;
        if (mPlayerParam == null) {
            mPlayerParam = new PlayerParam();
        }
        init();
    }

    private void init() {
        realInit();
        if (mDataSource != null) {
            setDataSource(mDataSource);
        }
        if (mSurface != null) {
            setSurfaceView(mSurface);
        }
        seek(mTimeInMs);
        mIsInitialing = false;
    }

    protected abstract void realInit();

    @Override
    public PlayerState getState() {
        return mState;
    }

    @Override
    public int getBufferingPercent() {
        return mBufferingPercent;
    }

    @Override
    public void release() {
        mBufferingPercent = 0;
        mIsInitialing = false;
        mListener = null;
        mHandler.removeMessages(EVENT_RECONNECT);
    }

    @Override
    public long getDuration() {
        if (mState.value <= PlayerState.ERROR.value) {
            return 0;
        }
        return doGetDuration();
    }

    protected abstract long doGetDuration();

    @Override
    public long getProgress() {
        if (mState.value <= PlayerState.ERROR.value) {
            return 0;
        }
        return doGetProgress();
    }

    protected abstract long doGetProgress();

    @Override
    public void setPlayerListener(IPlayerListener listener) {
        mListener = listener;
    }

    @Override
    public void removePlayerListener(IPlayerListener listener) {
        mListener = null;
    }

    protected void notifyVideoSizeChanged(int width, int height) {
        if (mListener != null) {
            mListener.onVideoSizeChanged(width, height);
        }
    }

    protected void notifyBuffering(int percent) {
        mBufferingPercent = percent;
        if (mListener != null) {
            mListener.onBuffering(percent);
        }
    }

    protected void notifyFirstRenderStart() {
        if (mListener != null) {
            mListener.onFirstRenderStart();
        }
        notifyPlaying();
    }

    protected void notifyPlaying() {
        mState = PlayerState.PLAYING;
        if (mListener != null) {
            mListener.onPlaying();
        }
    }

    protected void notifyBufferingStart() {
        mState = PlayerState.BUFFERING_START;
        if (mListener != null) {
            mListener.onBufferingStart();
        }
    }

    protected void notifyBufferingEnd() {
        mState = PlayerState.BUFFERING_END;
        mState = PlayerState.PLAYING;
        if (mListener != null) {
            mListener.onBufferingEnd();
            mListener.onPlaying();
        }
    }

    protected void notifyVideoRotationChanged(int rotation) {
        if (mListener != null) {
            mListener.onVideoRotationChanged(rotation);
        }
    }

    protected void notifyPaused() {
        mState = PlayerState.PAUSED;
        if (mListener != null) {
            mListener.onPaused();
        }
    }

    protected void notifyStopped() {
        mState = PlayerState.STOPPED;
        if (mListener != null) {
            mListener.onStopped();
        }
    }

    protected void notifyError(String msg, int type) {
        mState = PlayerState.ERROR;
        if (mPlayerParam.mIsReconnectOnError) {
            mHandler.removeMessages(EVENT_RECONNECT);
            mHandler.sendEmptyMessageDelayed(EVENT_RECONNECT, mPlayerParam.mReconnectTime * 1000);
        }
        if (mListener != null) {
            mListener.onError(msg, type);
        }
    }

    protected void notifyComplete() {
        mState = PlayerState.COMPLETE;
        if (mListener != null) {
            mListener.onComplete();
        }
    }

    protected void notifyPrepared() {
        mState = PlayerState.PREPARED;
        if (mIsStartMethodInvoked && !mPlayerParam.mIsStartOnPrepared) {
            start();
        }
        if (mListener != null) {
            mListener.onPrepared();
        }

    }

    @Override
    public void setDataSource(String path) {
        mDataSource = path;
        if (mDataSource != null && mDataSource.startsWith("https://")) {
            mDataSource = mDataSource.replace("https://", "http://");
        }
        doSetDataSource(mDataSource);
        notifyBuffering(0);
    }

    protected abstract void doSetDataSource(String dataSource);

    @Override
    public void setSurfaceView(Surface surface) {
        mSurface = surface;
    }

    @Override
    public void seek(long timeInMs) {
        mTimeInMs = timeInMs;
    }

    @Override
    public void pause() {
        if (mIsWorking) {
            return;
        }
        mIsWorking = true;
        if (mState == PlayerState.PLAYING) {
            doPause();
            notifyPaused();
        }
        mIsWorking = false;
    }

    protected abstract void doPause();

    protected abstract void doResume();

    protected abstract void doStart();

    protected abstract void doStop();

    @Override
    public void stop() {
        if (mIsWorking) {
            return;
        }
        mIsWorking = true;
        doStop();
        notifyStopped();
        mIsWorking = false;
    }

    @Override
    public void start() {
        if (mIsWorking) {
            return;
        }
        mIsStartMethodInvoked = true;
        mIsWorking = true;
        if (mState == PlayerState.PAUSED) {
            doResume();
            notifyPlaying();
        } else if (mState == PlayerState.PREPARED ||
                mState == PlayerState.COMPLETE ||
                mState == PlayerState.STOPPED) {
            doStart();
        }
        mIsWorking = false;
    }
}

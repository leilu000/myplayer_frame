package player.manager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.util.List;

import player.base.inter.IPlayer;
import player.base.inter.IPlayerListener;
import player.bean.DisplayMode;
import player.bean.SimplePlayerListener;

/**
 * 主要处理消息分发的，因为播放器的回调都是在子线程里面，
 * 这里统一把这些回调通过主线程的方式回调出去
 * Created by ll on 2019/12/5.
 */
public class PlayerEventDispatcher {

    private static final int EVENT_ON_BUFFERING = -1000;
    private static final int EVENT_ON_FIRST_RENDER_START = -1001;
    private static final int EVENT_ON_PLAYING = -1002;
    private static final int EVENT_ON_PREPARED = -1003;
    private static final int EVENT_ON_ERROR = -1004;
    private static final int EVENT_ON_COMPLETE = -1005;
    private static final int EVENT_ON_PAUSE = -1006;
    private static final int EVENT_ON_RESUME = -1007;
    private static final int EVENT_ON_BUFFERING_START = -1008;
    private static final int EVENT_ON_BUFFERING_END = -1009;
    private static final int EVENT_ON_VIDEO_SIZE_CHANGED = -1010;
    private static final int EVENT_ON_STOPPED = -1011;
    private static final int EVENT_ON_VIDEO_ROTATION_CHANGED = -1012;

    private List<SimplePlayerListener> mListenerList;
    private IPlayer mPlayer;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_ON_STOPPED:
                    notifyStopped();
                    break;
                case EVENT_ON_BUFFERING:
                    notifyBuffering(msg.arg1);
                    break;
                case EVENT_ON_FIRST_RENDER_START:
                    notifyFirstRenderStart();
                    break;
                case EVENT_ON_PLAYING:
                    notifyPlaying();
                    break;
                case EVENT_ON_PREPARED:
                    notifyPrepared();
                    break;
                case EVENT_ON_ERROR:
                    notifyError((String) msg.obj, msg.arg1);
                    break;
                case EVENT_ON_COMPLETE:
                    notifyComplete();
                    break;
                case EVENT_ON_PAUSE:
                    notifyPause();
                    break;
                case EVENT_ON_RESUME:
                    notifyResume();
                    break;
                case EVENT_ON_BUFFERING_START:
                    notifyBufferingStart();
                    break;
                case EVENT_ON_BUFFERING_END:
                    notifyBufferingEnd();
                    break;
                case EVENT_ON_VIDEO_SIZE_CHANGED:
                    notifyVideoSizeChanged(msg.arg1, msg.arg2);
                    break;
                case EVENT_ON_VIDEO_ROTATION_CHANGED:
                    notifyVideoRotationChanged(msg.arg1);
                    break;
            }
        }
    };

    private void notifyVideoRotationChanged(int rotaion) {
        for (IPlayerListener listener : mListenerList) {
            listener.onVideoRotationChanged(rotaion);
        }
    }

    private void notifyVideoSizeChanged(int width, int height) {
        for (IPlayerListener listener : mListenerList) {
            listener.onVideoSizeChanged(width, height);
        }
    }

    private void notifyBufferingEnd() {
        for (IPlayerListener listener : mListenerList) {
            listener.onBufferingEnd();
        }
    }

    private void notifyBufferingStart() {
        for (IPlayerListener listener : mListenerList) {
            listener.onBufferingStart();
        }
    }

    private void notifyResume() {
        for (IPlayerListener listener : mListenerList) {
            listener.onResume();
        }
    }

    private void notifyPause() {
        for (IPlayerListener listener : mListenerList) {
            listener.onPaused();
        }
    }

    private void notifyStopped() {
        for (IPlayerListener listener : mListenerList) {
            listener.onStopped();
        }
    }

    private void notifyBuffering(int percent) {
        for (IPlayerListener listener : mListenerList) {
            listener.onBuffering(percent);
        }
    }

    private void notifyFirstRenderStart() {
        for (IPlayerListener listener : mListenerList) {
            listener.onFirstRenderStart();
        }
    }

    public void notifyScreenOrientationChanged(DisplayMode orientation) {
        for (IPlayerListener listener : mListenerList) {
            listener.onDisplayModeChanged(orientation);
        }
    }

    private void notifyPlaying() {
        for (IPlayerListener listener : mListenerList) {
            listener.onPlaying();
        }
    }

    private void notifyPrepared() {
        for (IPlayerListener listener : mListenerList) {
            listener.onPrepared();
        }
    }

    private void notifyError(String error, int type) {
        for (IPlayerListener listener : mListenerList) {
            listener.onError(error, type);
        }
    }

    private void notifyComplete() {
        for (IPlayerListener listener : mListenerList) {
            listener.onComplete();
        }
    }

    public PlayerEventDispatcher(List<SimplePlayerListener> listenerList) {
        mListenerList = listenerList;
    }

    public void init(IPlayer player) {
        mPlayer = player;
        mPlayer.setPlayerListener(mPlayerListener);
    }

    public void release() {
        mPlayer.removePlayerListener(mPlayerListener);
        mHandler.removeMessages(EVENT_ON_BUFFERING);
        mHandler.removeMessages(EVENT_ON_FIRST_RENDER_START);
        mHandler.removeMessages(EVENT_ON_PLAYING);
        mHandler.removeMessages(EVENT_ON_PREPARED);
        mHandler.removeMessages(EVENT_ON_ERROR);
        mHandler.removeMessages(EVENT_ON_COMPLETE);
        mHandler.removeMessages(EVENT_ON_PAUSE);
        mHandler.removeMessages(EVENT_ON_RESUME);
        mHandler.removeMessages(EVENT_ON_BUFFERING_START);
        mHandler.removeMessages(EVENT_ON_BUFFERING_END);
        mHandler.removeMessages(EVENT_ON_VIDEO_SIZE_CHANGED);
    }

    private IPlayerListener mPlayerListener = new IPlayerListener() {

        @Override
        public void onVideoRotationChanged(int rotation) {
            Message message = Message.obtain();
            message.what = EVENT_ON_VIDEO_ROTATION_CHANGED;
            message.arg1 = rotation;
            mHandler.sendMessage(message);
        }

        @Override
        public void onDisplayModeChanged(DisplayMode orientation) {

        }

        @Override
        public void onStopped() {
            mHandler.sendEmptyMessage(EVENT_ON_STOPPED);
        }

        @Override
        public void onVideoSizeChanged(int width, int height) {
            Message message = Message.obtain();
            message.what = EVENT_ON_VIDEO_SIZE_CHANGED;
            message.arg1 = width;
            message.arg2 = height;
            mHandler.sendMessage(message);
        }

        @Override
        public void onBufferingStart() {
            mHandler.sendEmptyMessage(EVENT_ON_BUFFERING_START);
        }

        @Override
        public void onBufferingEnd() {
            mHandler.sendEmptyMessage(EVENT_ON_BUFFERING_END);
        }

        @Override
        public void onPaused() {
            mHandler.sendEmptyMessage(EVENT_ON_PAUSE);
        }

        @Override
        public void onResume() {
            mHandler.sendEmptyMessage(EVENT_ON_RESUME);
        }

        @Override
        public void onBuffering(int percent) {
            Message message = Message.obtain();
            message.what = EVENT_ON_BUFFERING;
            message.arg1 = percent;
            mHandler.sendMessage(message);
        }

        @Override
        public void onFirstRenderStart() {
            mHandler.sendEmptyMessage(EVENT_ON_FIRST_RENDER_START);
        }

        @Override
        public void onPlaying() {
            mHandler.sendEmptyMessage(EVENT_ON_PLAYING);
        }

        @Override
        public void onPrepared() {
            mHandler.sendEmptyMessage(EVENT_ON_PREPARED);
        }

        @Override
        public void onError(String msg, int type) {
            Message message = Message.obtain();
            message.what = EVENT_ON_ERROR;
            if (TextUtils.isEmpty(msg)) {
                msg = "unKnow";
            }
            message.obj = msg;
            message.arg1 = type;
            mHandler.sendMessage(message);
        }

        @Override
        public void onComplete() {
            mHandler.sendEmptyMessage(EVENT_ON_COMPLETE);
        }
    };


}

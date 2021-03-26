package player.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.INotificationSideChannel;
import android.text.method.Touch;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.core.view.ViewConfigurationCompat;

/**
 * PlayerFrame
 *
 * <p>Description: </p>
 * <br>
 *
 * <p>Copyright: Copyright (c) 2021</p>
 *
 * @author leilu.lei@alibaba-inc.com
 * @version 1.0
 * 3/26/21 5:33 PM
 */
public class TouchClickHelper {

    private int mClickCount;
    private MyHandler mHandler;
    private HandlerThread mHandlerThread;
    private Listener mListener;

    public TouchClickHelper(Listener listener) {
        mListener = listener;
        mHandlerThread = new HandlerThread("TouchClickHelperThread");
        mHandlerThread.start();
        mHandler = new MyHandler(mHandlerThread.getLooper(), this);
    }

    public void release() {
        if (mHandlerThread != null) {
            mHandler.removeCallbacksAndMessages(null);
          //  mHandlerThread.quit();
        }
    }

    private static class MyHandler extends Handler {

        private TouchClickHelper mTouchClickHelper;

        public MyHandler(Looper looper, TouchClickHelper mTouchClickHelper) {
            super(looper);
            this.mTouchClickHelper = mTouchClickHelper;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            mTouchClickHelper.handleMessage();
        }
    }

    private void handleMessage() {
        if (mClickCount == 1) {
            mListener.onClick();
        } else if (mClickCount >= 2) {
            mListener.onDoubleClick();
        }
        mClickCount = 0;
    }

    public void onTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mClickCount++;
                mHandler.removeCallbacksAndMessages(null);
                break;
            case MotionEvent.ACTION_UP:
                mHandler.sendEmptyMessageAtTime(0, 150);
                break;
            default:
                break;
        }
    }

    public interface Listener {
        void onClick();

        void onDoubleClick();
    }
}

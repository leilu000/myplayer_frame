package player.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.MotionEvent;
import android.view.View;

/**
 * 播放器的渲染载体基类，所有的渲染载体都应该继承此类扩展
 * Created by ll on 2019/12/7.
 */
public class FloatWindowMoveHelper {
    private float mLastX;
    private float mLastY;
    private final int mParentWidth;
    private final int mParentHeight;
    private AnimatorSet mAnimatorSet;
    private Listener mListener;

    public FloatWindowMoveHelper(int parentWidth, int parentHeight, Listener listener) {
        mParentWidth = parentWidth;
        mParentHeight = parentHeight;
        mListener = listener;
    }

    public void clear() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        mListener = null;
    }

    public boolean onTouch(View view, MotionEvent event) {
        if (mAnimatorSet != null) {
            return false;
        }
        float x = event.getRawX();
        float y = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mLastX;
                float deltaY = y - mLastY;
                float tranX = view.getX() + deltaX;
                float tranY = view.getY() + deltaY;
                view.setX(tranX);
                view.setY(tranY);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onActionUP(view);
                break;
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }

    private void onActionUP(View view) {
        float x = view.getX();
        float y = view.getY();
        int width = view.getWidth();
        int height = view.getHeight();
        int statusBarHeight = Utils.getStatusBarHeight();
        // 右上
        if (x >= mParentWidth - width && y <= statusBarHeight) {
            move(view, mParentWidth - width, statusBarHeight);
        }
        // 右下
        else if (x >= mParentWidth - width && y >= mParentHeight - height) {
            move(view, mParentWidth - width, mParentHeight - height);
        }
        // 左上
        else if (x <= 0 && y <= statusBarHeight) {
            move(view, 0, statusBarHeight);
        }
        // 左下
        else if (x <= 0 && y >= mParentHeight - height) {
            move(view, 0, mParentHeight - height);
        }
        // 左
        else if (x <= 0) {
            move(view, 0, view.getY());
        }
        // 右
        else if (x >= mParentWidth - width) {
            move(view, mParentWidth - width, view.getY());
        }
        // 上
        else if (y <= Utils.getStatusBarHeight()) {
            move(view, view.getX(), Utils.getStatusBarHeight());
        }
        // 下
        else if (y >= mParentHeight - height) {
            move(view, view.getX(), mParentHeight - height);
        } else {
            move(view, x, y);
        }
    }

    public void move(View view, float destX, float destY) {
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.addListener(new MyListener());
        mAnimatorSet.setDuration(200);
        Animator xAnimator = ObjectAnimator.ofFloat(view, "X", view.getX(), destX);
        Animator yAnimator = ObjectAnimator.ofFloat(view, "Y", view.getY(), destY);
        mAnimatorSet.playTogether(xAnimator, yAnimator);
        mAnimatorSet.start();
    }

    private class MyListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {
            if (mListener != null) {
                mListener.onAnimationStart();
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mAnimatorSet = null;
            if (mListener != null) {
                mListener.onAnimationEnd();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mAnimatorSet = null;
            if (mListener != null) {
                mListener.onAnimationCancel();
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    public interface Listener {
        void onAnimationStart();

        void onAnimationEnd();

        void onAnimationCancel();

    }
}

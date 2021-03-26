//package com.leilu.playerframe;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.res.Configuration;
//import android.support.v4.view.ViewCompat;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.ViewConfiguration;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//
//import cn.ninegame.gamemanager.business.common.livestreaming.floatingwindow.libarary.FloatPlayerManager;
//import cn.ninegame.gamemanager.business.common.livestreaming.floatingwindow.libarary.MotionVelocityUtil;
//import cn.ninegame.gamemanager.business.common.livestreaming.floatingwindow.libarary.runner.ICarrier;
//import cn.ninegame.gamemanager.business.common.livestreaming.floatingwindow.libarary.runner.OnceRunnable;
//import cn.ninegame.gamemanager.business.common.livestreaming.floatingwindow.libarary.runner.ScrollRunner;
//import cn.ninegame.gamemanager.business.common.livestreaming.floatingwindow.window.view.DensityUtil;
//import cn.ninegame.gamemanager.business.common.livestreaming.floatingwindow.window.view.FloatAnchorLayout;
//
//public class FloatWindow extends FrameLayout   {
//
//    private ImageView imageView;
//    private WindowManager.LayoutParams mLayoutParams;
//    private WindowManager windowManager;
//    private boolean isFirst = true;
//    private boolean isAdded = false;
//    private int mTouchSlop;
//    private boolean isClick;
//    private int mDownX, mDownY, mLastX, mLastY;
//    private int mVelocityX, mVelocityY;
//    private boolean sleep = false;
//    private boolean mHideHalfLater = true;
//    private boolean mLayoutChanged = false;
//    private int mSleepX = -1;
//
//
//
//    private static int sLastOrientation = Configuration.ORIENTATION_UNDEFINED;
//    private FrameLayout mContainer;
//    public FloatWindow(Context context) {
//        super(context);
//        init(context);
//    }
//
//    private void init(Context context) {
//        addView(icon, new ViewGroup.LayoutParams(mSize.getWidth(), mSize.getHeight()));
//        initLayoutParams(context);
//        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
//        mRunner = new ScrollRunner(this);
//        mVelocity = new MotionVelocityUtil(context);
//
//        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//    }
//
//    private void initLayoutParams(Context context) {
////        mLayoutParams.copyFrom(mFloatAnchorLayout.getParams());
//        mLayoutParams = mFloatAnchorLayout.getParams();
//    }
//
//    @Override
//    protected void onWindowVisibilityChanged(int visibility) {
//        super.onWindowVisibilityChanged(visibility);
//        if (visibility == VISIBLE) {
//            onConfigurationChanged(null);
//        }
//    }
//
//    public void attachToWindow(WindowManager windowManager) {
//        this.windowManager = windowManager;
//        if (!isAdded) {
//            try {
//                windowManager.addView(this, mLayoutParams);
//            } catch (Exception e) {
//                Log.e("FloatBall", e.toString());
//            }
//            isAdded = true;
//        }
//    }
//
//    public void detachFromWindow(WindowManager windowManager) {
//        this.windowManager = null;
//        if (isAdded) {
//            removeSleepRunnable();
//            if (getContext() instanceof Activity) {
//                windowManager.removeViewImmediate(this);
//            } else {
//                windowManager.removeView(this);
//            }
//            isAdded = false;
//            sleep = false;
//        }
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int height = getMeasuredHeight();
//        int width = getMeasuredWidth();
//
//        int curX = mLayoutParams.x;
//        if (sleep && curX != mSleepX && !mRunner.isRunning()) {
//            sleep = false;
//            postSleepRunnable();
//        }
//        if (mRunner.isRunning()) {
//            mLayoutChanged = false;
//        }
//        if (height != 0 && isFirst || mLayoutChanged) {
//            if (isFirst) {
//                location();
//                postSleepRunnable();
//            } else {
//                moveToEdge(false, sleep);
//            }
//            isFirst = false;
//            mLayoutChanged = false;
//        }
//    }
//
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//    }
//
//    private void location() {
//        if (windowManager != null && ViewCompat.isAttachedToWindow(mContainer)) {
//            windowManager.updateViewLayout(mContainer, mLayoutParams);
//        }
//    }
//
//    @Override
//    protected void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        mLayoutChanged = true;
////        floatBallManager.onConfigurationChanged(newConfig);
//////        moveToEdge(false, false);
////        if (windowManager != null) {
////            mLayoutParams.x = 0;
////            mLayoutParams.y = 100;
////            windowManager.updateViewLayout(mContainer, mLayoutParams);
////        }
////        postSleepRunnable();
//    }
//
//    public void onLayoutChange() {
//        mLayoutChanged = true;
//        requestLayout();
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getAction();
//        int x = (int) event.getRawX();
//        int y = (int) event.getRawY();
//        mVelocity.acquireVelocityTracker(event);
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                touchDown(x, y);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                touchMove(x, y);
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                touchUp();
//                break;
//        }
//        return true;
//    }
//
//    private void touchDown(int x, int y) {
//        mDownX = x;
//        mDownY = y;
//        mLastX = mDownX;
//        mLastY = mDownY;
//        isClick = true;
//        removeSleepRunnable();
//        //setAlpha(1.0f);
//        //FloatWindow.this.animate().alpha(1.0f).setDuration(200).start();
//    }
//
//    private void touchMove(int x, int y) {
//        int totalDeltaX = x - mDownX;
//        int totalDeltaY = y - mDownY;
//        int deltaX = x - mLastX;
//        int deltaY = y - mLastY;
//        if (Math.abs(totalDeltaX) > mTouchSlop || Math.abs(totalDeltaY) > mTouchSlop) {
//            isClick = false;
//        }
//        mLastX = x;
//        mLastY = y;
//        if (!isClick) {
//            onMove(deltaX, deltaY);
//        }
//    }
//
//    private void touchUp() {
//        moveFromMenu = false;
//        mVelocity.computeCurrentVelocity();
//        mVelocityX = (int) mVelocity.getXVelocity();
//        mVelocityY = (int) mVelocity.getYVelocity();
//        mVelocity.releaseVelocityTracker();
//        if (sleep) {
//            wakeUp();
//            onClick();
//        } else {
//            if (isClick) {
//                onClick();
//            } else {
//                moveToEdge(true, false);
//            }
//        }
//        mVelocityX = 0;
//        mVelocityY = 0;
//    }
//
//    private void moveToX(boolean smooth, int destX) {
//        int statusBarHeight = floatBallManager.getStatusBarHeight();
//        final int screenHeight = floatBallManager.mScreenHeight - statusBarHeight;
//        int height = getHeight();
//        int destY = 0;
//        if (mLayoutParams.y < 0) {
//            destY = 0 - mLayoutParams.y;
//        } else if (mLayoutParams.y > screenHeight - height - DensityUtil.dip2px(getContext(), 70)) {
//            destY = screenHeight - height - mLayoutParams.y - DensityUtil.dip2px(getContext(), 70);
//        }
//        if (smooth) {
//            int dx = destX - mLayoutParams.x;
//            int duration = getScrollDuration(Math.abs(dx));
//            mRunner.start(dx, destY, duration);
//        } else {
//            onMove(destX - mLayoutParams.x, destY);
//            postSleepRunnable();
//        }
//    }
//
//    private void wakeUp() {
//        final int screenWidth = floatBallManager.mScreenWidth;
//        int width = getWidth();
//        int halfWidth = width / 2;
//        int centerX = (screenWidth / 2 - halfWidth);
//        int destX;
//        destX = mLayoutParams.x < centerX ? 0 : screenWidth - width;
//        sleep = false;
//        moveToX(true, destX);
//    }
//
//    private void moveToEdge(boolean smooth, boolean forceSleep) {
//        final int screenWidth = floatBallManager.mScreenWidth;
//        int width = getWidth();
//        int halfWidth = width /2;
//        int centerX = (screenWidth / 2 - halfWidth);
//        int destX;
//        final int minVelocity = mVelocity.getMinVelocity();
//        if (mLayoutParams.x < centerX) {
//            //sleep = forceSleep || Math.abs(mVelocityX) > minVelocity && mVelocityX < 0 || mLayoutParams.x < 0;
//            destX = sleep ? -halfWidth : 0;
//            //moveToX(smooth, destX+150);
//            FloatPlayerManager.updateFloatEdge(false);
//        } else {
//            //sleep = forceSleep || Math.abs(mVelocityX) > minVelocity && mVelocityX > 0 || mLayoutParams.x > screenWidth - width;
//            destX = sleep ? screenWidth - halfWidth : screenWidth - width;
//            FloatPlayerManager.updateFloatEdge(true);
//            //moveToX(smooth, destX-20);
//        }
//        if (sleep) {
//            mSleepX = destX;
//        }
//        // 禁止拖拽到屏幕内侧
//        if (destX < 0) {
//            destX = 0;
//        }
//        if (destX > screenWidth) {
//            destX = screenWidth - width;
//        }
//        moveToX(smooth, destX);
//    }
//
//    private int getScrollDuration(int distance) {
//        return (int) (250 * (1.0f * distance / 800));
//    }
//
//    private void onMove(int deltaX, int deltaY) {
//        mLayoutParams.x += deltaX;
//        mLayoutParams.y += deltaY;
//        if (windowManager != null && ViewCompat.isAttachedToWindow(mContainer)) {
//            floatBallManager.saveFloatBallPostion(mLayoutParams.x, mLayoutParams.y);
//            windowManager.updateViewLayout(mContainer, mLayoutParams);
//        }
//    }
//
//    @Override
//    public void onMove(int lastX, int lastY, int curX, int curY) {
//        onMove(curX - lastX, curY - lastY);
//    }
//
//    @Override
//    public void onDone() {
//        postSleepRunnable();
//    }
//
//    private void moveTo(int x, int y) {
//        mLayoutParams.x += x - mLayoutParams.x;
//        mLayoutParams.y += y - mLayoutParams.y;
//        if (windowManager != null && ViewCompat.isAttachedToWindow(mContainer)) {
//            windowManager.updateViewLayout(this, mLayoutParams);
//        }
//    }
//
//
//    private boolean moveFromMenu = false;
//    public void resetTouchSlop() {
//        //mTouchSlop = Integer.MAX_VALUE;
//        //mDownX = 0;
//        //mDownY = 0;
//        //isClick = false;
//        moveFromMenu = true;
//    }
//
//
//
//
//    public void postSleepRunnable() {
//        if (mHideHalfLater) {
//            //mSleepRunnable.postDelaySelf(this, 2000);
//        }
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        removeSleepRunnable();
//    }
//}

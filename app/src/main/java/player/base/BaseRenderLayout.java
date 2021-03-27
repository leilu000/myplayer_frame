package player.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.UiThread;

import player.base.inter.IPlayer;
import player.base.inter.OnFloatWindowClickListener;
import player.bean.DisplayMode;
import player.manager.Config;
import player.util.FloatWindowMoveHelper;
import player.util.TouchClickHelper;
import player.util.Utils;
import player.util.ViewScaleUtil;


/**
 * 播放器的渲染载体基类，所有的渲染载体都应该继承此类扩展
 * Created by ll on 2019/12/6.
 */
public abstract class BaseRenderLayout extends FrameLayout {

    private ViewScaleUtil.ScaleMode mScaleMode = ViewScaleUtil.ScaleMode.AspectFit;
    private int mImageWidth;
    private int mImageHeight;
    protected IPlayer mPlayer;
    private int mVideoRotation;
    private DisplayMode mDisplayMode;
    private FloatWindowMoveHelper mFloatWindowMoveHelper;
    private boolean mSaveFloatWindowPosition;
    private TouchClickHelper mTouchClickHelper;
    private OnFloatWindowClickListener mOnTinyWindowClickListener;

    public BaseRenderLayout(Context context) {
        this(context, null);
    }

    public BaseRenderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRenderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Color.BLACK);
    }

    public void setDisplayMode(DisplayMode displayMode) {
        mDisplayMode = displayMode;
    }

    public void setSaveFloatWindowPosition(boolean saveTinyWindowPosition) {
        mSaveFloatWindowPosition = saveTinyWindowPosition;
    }

    public void setTinyWindowClickListener(OnFloatWindowClickListener listener) {
        mOnTinyWindowClickListener = listener;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int childCount = getChildCount();
        if (childCount == 0 || (mImageWidth == 0 || mImageHeight == 0)) {
            return;
        }
        if (childCount > 1) {
            throw new RuntimeException("The layout can only have one child View !");
        }
        View childView = getChildAt(0);
        ViewScaleUtil.Size size = ViewScaleUtil.calcFitSize(mImageWidth, mImageHeight,
                getMeasuredWidth(), getMeasuredHeight(), mScaleMode);
        FrameLayout.LayoutParams params = (LayoutParams) childView.getLayoutParams();
        params.width = size.width;
        params.height = size.height;
        params.leftMargin = size.x;
        params.topMargin = size.y;
        childView.layout(size.x, size.y, size.x + size.width, size.y + size.height);
    }


    @UiThread
    protected void setScaleMode(ViewScaleUtil.ScaleMode mode, int imageWidth, int imageHeight) {
        if (mode != null || mImageWidth != imageWidth || mImageHeight != imageHeight) {
            mScaleMode = mode;
            mImageWidth = imageWidth;
            mImageHeight = imageHeight;
            requestLayout();
        }
    }

    @UiThread
    public void setScaleMode(ViewScaleUtil.ScaleMode mode) {
        setScaleMode(mode, mImageWidth, mImageHeight);
    }

    @UiThread
    public void updateImageSize(int width, int height) {
        setScaleMode(mScaleMode, width, height);
    }

    public void attachPlayer(IPlayer player) {
        detachPlayer();
        mPlayer = player;
    }

    public void detachPlayer() {
        mPlayer = null;
    }

    public abstract Bitmap capture();

    @UiThread
    public void onVideoRotationChanged(int rotation) {
        if (mVideoRotation != rotation) {
            Log.i("leilu", "onVideoRotationChanged,rotation:" + rotation);
            mVideoRotation = rotation;
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            if (rotation == 90 || rotation == 270) {
                int temp = width;
                width = height;
                height = temp;
            }
            View childView = getChildAt(0);
            ViewScaleUtil.Size size = ViewScaleUtil.calcFitSize(mImageWidth, mImageHeight, width, height, mScaleMode);
            FrameLayout.LayoutParams params = (LayoutParams) childView.getLayoutParams();
            params.width = 0;
            params.height = 0;
            params.gravity = Gravity.CENTER;
            childView.setLayoutParams(params);
            childView.setRotation(rotation);
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (mDisplayMode != DisplayMode.INNER_ACTIVITY_TINY_WINDOW) {
            return false;
        }
        mTouchClickHelper.onTouch(event);
        initTinyWindowHelper();
        return mFloatWindowMoveHelper.onTouch(v, event);
    }

    private void initTinyWindowHelper() {
        if (mFloatWindowMoveHelper == null) {
            int parentWidth = Utils.getScreenWidth();
            int parentHeight = Utils.getScreenHeight() + Utils.getStatusBarHeight();
            mFloatWindowMoveHelper = new FloatWindowMoveHelper(parentWidth, parentHeight, mTinyWindowListener);
        }
        if (mTouchClickHelper == null) {
            mTouchClickHelper = new TouchClickHelper(mTouchClickHelperListener);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mDisplayMode == DisplayMode.INNER_ACTIVITY_TINY_WINDOW) {
            final float[] savedPosition = Config.getInstance().getTinyWindowPosition();
            initTinyWindowHelper();
            mFloatWindowMoveHelper.move(this, savedPosition[0], savedPosition[1]);

        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mFloatWindowMoveHelper != null) {
            mFloatWindowMoveHelper.clear();
        }
        if (mTouchClickHelper != null) {
            mTouchClickHelper.release();
        }
    }

    private void savePositionIfNeed() {
        if (mSaveFloatWindowPosition) {
            Config.getInstance().saveTinyWindowPosition(getX(), getY());
        }
    }

    private TouchClickHelper.Listener mTouchClickHelperListener = new TouchClickHelper.Listener() {
        @Override
        public void onClick() {
            if (mOnTinyWindowClickListener != null) {
                mOnTinyWindowClickListener.onSingleClick();
            }
        }

        @Override
        public void onDoubleClick() {
            if (mOnTinyWindowClickListener != null) {
                mOnTinyWindowClickListener.onDoubleClick();
            }
        }
    };

    private final FloatWindowMoveHelper.Listener mTinyWindowListener = new FloatWindowMoveHelper.Listener() {
        @Override
        public void onAnimationStart() {

        }

        @Override
        public void onAnimationEnd() {
            savePositionIfNeed();
        }

        @Override
        public void onAnimationCancel() {
            savePositionIfNeed();
        }
    };
}

package player.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.UiThread;

import player.base.inter.IPlayer;
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
    private int mVideoRotaion;

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

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getChildCount() == 0 || (mImageWidth == 0 || mImageHeight == 0)) {
            return;
        }
        if (getChildCount() > 1) {
            throw new RuntimeException("The layout can only have one child View !");
        }
        View childView = getChildAt(0);
        ViewScaleUtil.Size size = ViewScaleUtil.calcFitSize(mImageWidth, mImageHeight, getMeasuredWidth(), getMeasuredHeight(), mScaleMode);
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
        dettachPlayer();
        mPlayer = player;
    }

    public void dettachPlayer() {
        mPlayer = null;
    }

    public abstract Bitmap capture();

    @UiThread
    public void onVideoRotationChanged(int rotation) {
        if (mVideoRotaion != rotation) {
            Log.i("leilu", "onVideoRotationChanged,rotation:" + rotation);
            mVideoRotaion = rotation;
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
}
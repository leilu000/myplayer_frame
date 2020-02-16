package player.base;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
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
public abstract class BasePlayerLayout extends FrameLayout {

    private ViewScaleUtil.ScaleMode mScaleMode = ViewScaleUtil.ScaleMode.AspectFit;
    private int mImageWidth;
    private int mImageHeight;
    protected IPlayer mPlayer;

    public BasePlayerLayout(Context context) {
        this(context, null);
    }

    public BasePlayerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasePlayerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() > 1) {
            throw new RuntimeException("The child count is more than one !");
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getChildCount() == 0 || (mImageWidth == 0 || mImageHeight == 0)) {
            return;
        }
        View childView = getChildAt(0);
        ViewScaleUtil.Size size = ViewScaleUtil.calcFitSize(mImageWidth, mImageHeight, getMeasuredWidth(), getMeasuredHeight(), mScaleMode);
        FrameLayout.LayoutParams params = (LayoutParams) childView.getLayoutParams();
        params.gravity = Gravity.TOP;
        params.width = size.width;
        params.height = size.height;
        params.leftMargin = size.x;
        params.topMargin = size.y;
        childView.setLayoutParams(params);
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
}

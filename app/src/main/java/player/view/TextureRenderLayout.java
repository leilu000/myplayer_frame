package player.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import player.base.BaseRenderLayout;
import player.base.inter.IPlayer;


/**
 * 自带的播放器渲染载体，如果需要自定义则参考此类继承BasePlayerLayout
 * Created by ll on 2019/12/6.
 */
public class TextureRenderLayout extends BaseRenderLayout implements TextureView.SurfaceTextureListener {

    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    private TextureView mTextureView;

    public TextureRenderLayout(Context context) {
        this(context, null);
    }

    public TextureRenderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextureRenderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTextureView = new TextureView(context);
        mTextureView.setSurfaceTextureListener(this);
        addView(mTextureView);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surface;
            mSurface = new Surface(surface);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mTextureView.setSurfaceTexture(mSurfaceTexture);
        }
        updatePlayerSurface();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return mSurfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPlayer != null) {
            // mPlayer.setSurfaceView(null);
        }
    }

    @Override
    public void dettachPlayer() {
        super.dettachPlayer();
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mSurface != null) {
            mSurface.release();
        }
    }

    @Override
    public Bitmap capture() {
        return mTextureView.getBitmap();
    }

    @Override
    public void attachPlayer(IPlayer player) {
        super.attachPlayer(player);
        updatePlayerSurface();
    }

    private void updatePlayerSurface() {
        if (mPlayer != null) {
            mPlayer.setSurfaceView(mSurface);
        }
    }
}

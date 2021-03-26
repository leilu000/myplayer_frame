package player.player;

import android.view.Surface;

import java.io.IOException;

import player.base.BaseYomePlayer;
import player.bean.PlayerParam;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


/**
 * ijkplayer
 * Created by ll on 2019/12/5.
 */
public class IjkYomePlayer extends BaseYomePlayer {

    private IjkMediaPlayer mPlayer;

    public IjkYomePlayer(PlayerParam playerParam) {
        super((playerParam));
    }

    @Override
    protected void realInit() {
        initPlayer();
        initOnCompletionListener();
        initOnErrorListener();
        initOnPreparedListener();
        initOnInfoListener();
        initOnBufferingUpdateListener();
        initVideoSizeChangedListener();
    }

    private void initPlayer() {
        mPlayer = new IjkMediaPlayer();
        mPlayer.setScreenOnWhilePlaying(true);
        mPlayer.setLooping(mPlayerParam.mIsLoop);
        //  Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
        int isMediaCodec = mPlayerParam.mIsHardDecode ? 1 : 0;
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", isMediaCodec);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", mPlayerParam.mMaxBufferSize);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", mPlayerParam.mMinFrames);
    }

    private void initVideoSizeChangedListener() {
        mPlayer.setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int width, int height, int sar_num, int sar_den) {
                notifyVideoSizeChanged(width, height);
            }
        });

    }

    private void initOnBufferingUpdateListener() {
        mPlayer.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
                notifyBuffering(i);
            }
        });

    }

    private void initOnInfoListener() {
        mPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
                switch (what) {
                    case IjkMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        notifyFirstRenderStart();
                        notifyPlaying();
                        break;
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        notifyBufferingStart();
                        break;
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        notifyBufferingEnd();
                        break;
                    case IjkMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                        notifyVideoRotationChanged(extra);
                        break;
                }
                return false;
            }
        });

    }

    private void initOnPreparedListener() {
        mPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                notifyPrepared();
                if (mPlayerParam.mIsStartOnPrepared) {
                    start();
                }
            }
        });

    }

    private void initOnErrorListener() {
        mPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                notifyError(String.valueOf(i), i1);
                return true;
            }
        });

    }

    private void initOnCompletionListener() {
        mPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                notifyComplete();
            }
        });
    }

    @Override
    protected void doSetDataSource(String dataSource) {
        if (mPlayer == null) {
            return;
        }
        try {
            mPlayer.setDataSource(dataSource);
            mPlayer.prepareAsync();
        } catch (IOException e) {
            notifyError(e.getMessage(), 0);
        }
    }

    @Override
    protected void doStart() {
        if (mPlayer != null) {
            mPlayer.start();
        }
    }

    @Override
    protected void doPause() {
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    @Override
    protected void doResume() {
        doStart();
    }

    @Override
    protected void doStop() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    @Override
    public void release() {
        super.release();
        doStop();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void setLoop(boolean isLoop) {
        mPlayerParam.mIsLoop = isLoop;
        if (mPlayer != null) {
            mPlayer.setLooping(isLoop);
        }
    }

    @Override
    public void seek(long timeInMs) {
        super.seek(timeInMs);
        if (mPlayer != null) {
            mPlayer.seekTo(timeInMs);
        }
    }

    @Override
    protected long doGetProgress() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    protected long doGetDuration() {
        if (mPlayer != null) {
            return mPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public void setSurfaceView(Surface surface) {
        super.setSurfaceView(surface);
        if (mPlayer != null) {
            mPlayer.setSurface(surface);
        }
    }

    @Override
    public void setVolume(float left, float right) {
        if (mPlayer != null) {
            mPlayer.setVolume(left, right);
        }
    }
}

package player.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;

import player.base.BasePlayer;
import player.bean.PlayerParam;

/**
 * 系统自带的MediaPlayer
 * Created by ll on 2019/12/5.
 */
public class AndroidPlayer extends BasePlayer {

    private MediaPlayer mPlayer;

    public AndroidPlayer(PlayerParam playerParam) {
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
        mPlayer = new MediaPlayer();
        mPlayer.setScreenOnWhilePlaying(true);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void initVideoSizeChangedListener() {
        mPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                notifyVideoSizeChanged(width, height);
            }
        });
    }

    private void initOnBufferingUpdateListener() {
        mPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                notifyBuffering(percent);
            }
        });
    }

    private void initOnInfoListener() {
        mPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        notifyFirstRenderStart();
                        notifyPlaying();
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        notifyBufferingStart();
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        notifyBufferingEnd();
                        break;
                }
                return false;
            }
        });
    }

    private void initOnPreparedListener() {
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                notifyPrepared();
                if (mPlayerParam.mIsStartOnPrepared) {
                    start();
                }
            }
        });
    }

    private void initOnErrorListener() {
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (what == -38) {
                    return false;
                }
                notifyError(String.valueOf(what), extra);
                return true;
            }
        });
    }

    private void initOnCompletionListener() {
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mPlayerParam.mIsLoop) {
                    mPlayer.start();
                    return;
                }
                notifyComplete();
            }
        });
    }


    @Override
    protected void doSetDataSource(String path) {
        if (mPlayer == null) {
            return;
        }
        try {
            mPlayer.reset();
            mPlayer.setDataSource(path);
            mPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("leilu", "AndroidPlayer,doSetDataSource:" + e.getMessage());
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
            mPlayer.seekTo((int) timeInMs);
        }
    }

    @Override
    public long getDuration() {
        if (mPlayer != null) {
            return mPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public long getProgress() {
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
    protected long doGetProgress() {
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

package player.manager;

import androidx.annotation.UiThread;

import com.leilu.playerframe.BaseApplication;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import player.base.BasePlayerControlView;
import player.base.BasePlayerLayout;
import player.base.inter.IPlayer;
import player.base.inter.IPlayerFactory;
import player.base.inter.ITinyWindowParamFactory;
import player.bean.DisplayMode;
import player.bean.PlayerParam;
import player.bean.PlayerState;
import player.bean.SimplePlayerListener;
import player.util.ViewScaleUtil;
import player.videocache.HttpProxyCacheServer;

/**
 * 通过此类创建播放器
 * Created by ll on 2019/12/5.
 */
public class NikoPlayer {

    private IPlayer mPlayer;
    private List<SimplePlayerListener> mPlayerListenerList;
    private PlayerEventDispatcher mPlayerEventDispatcher;
    private BasePlayerLayout mPlayerLayout;
    private BasePlayerControlView mControllerView;
    private DisplayModeController mDisplayModeController;
    private Builder mBuilder;
    private int mVideoWidth;
    private int mVideoHeight;
    private String mDataSource;

    private NikoPlayer(Builder builder) {
        mBuilder = builder;
        init();
    }

    /**
     * 添加播放器控制View
     *
     * @param view
     */
    @UiThread
    public NikoPlayer setPlayerControllerView(final BasePlayerControlView view) {
        removePlayerControllerView();
        mControllerView = view;
        if (mControllerView != null && mPlayer != null) {
            mControllerView.attachPlayerMgr(NikoPlayer.this);
            mDisplayModeController.attachPlayerControllerView(view);
        }
        return this;
    }

    /**
     * 移除播放器控制View
     */
    @UiThread
    public NikoPlayer removePlayerControllerView() {
        if (mControllerView != null) {
            mControllerView.detachPlayerMgr();
            mControllerView = null;
        }
        mDisplayModeController.detachPlayerControllerView();
        return this;
    }

    @UiThread
    public void setDisplayMode(DisplayMode mode) {
        checkPlayer();
        if (mode != null) {
            mBuilder.mDisplayMode = mode;
        }
        if (mDisplayModeController.setDisplayMode(mode)) {
            if (mControllerView != null) {
                mControllerView.attachPlayerMgr(this);
            }
            mPlayerEventDispatcher.notifyScreenOrientationChanged(getDisplayMode());
        }
    }


    /**
     * 在Activity的onBackPress中调用此方法，如果返回true，则从横哦屏变成竖屏
     *
     * @return true, 横屏回到竖屏
     */
    @UiThread
    public boolean onBackPress() {
        if (mDisplayModeController != null && mDisplayModeController.getDisplayMode() != DisplayMode.PORTRAIT) {
            setDisplayMode(DisplayMode.PORTRAIT);
            return true;
        }
        return false;
    }

    /**
     * 添加监听事件
     *
     * @param listener
     */
    @UiThread
    public NikoPlayer addPlayerListener(SimplePlayerListener listener) {
        if (listener != null && !mPlayerListenerList.contains(listener)) {
            mPlayerListenerList.add(listener);
            switch (getState()) {
                case ERROR:
                    listener.onError("unkown", 0);
                    break;
                case STOPPED:
                    listener.onStopped();
                    break;
                case PREPARED:
                    listener.onPrepared();
                    break;
                case BUFFERING_START:
                    listener.onBufferingStart();
                case BUFFERING_END:
                    listener.onBufferingEnd();
                    break;
                case PAUSED:
                    listener.onPaused();
                    break;
                case PLAYING:
                    listener.onPlaying();
                    break;
            }
        }
        return this;
    }

    /**
     * 移除监听事件
     *
     * @param listener
     */
    @UiThread
    public NikoPlayer removePlayerListener(SimplePlayerListener listener) {
        mPlayerListenerList.remove(listener);
        return this;
    }

    private PlayerParam createPlayerParam() {
        PlayerParam playerParam = new PlayerParam();
        playerParam.mIsHardDecode = mBuilder.mIsHardDecode;
        playerParam.mIsLoop = mBuilder.mIsLoop;
        playerParam.mIsStartOnPrepared = mBuilder.mIsStartOnPrepared;
        playerParam.mIsReconnectOnError = mBuilder.mIsReconnectOnError;
        playerParam.mMaxBufferSize = mBuilder.mMaxBufferSize;
        playerParam.mMinFrames = mBuilder.mMinFrames;
        playerParam.mReconnectTime = mBuilder.mReconnectTime;
        return playerParam;
    }


    private NikoPlayer init() {
        mPlayer = mBuilder.mPlayerFactory.createPlayer(mBuilder.mPlayerType, createPlayerParam());
        if (mPlayer == null) {
            throw new IllegalStateException("The player must be not null !");
        }
        mDisplayModeController = new DisplayModeController(mBuilder.mTinyWindowParamFactory);
        mPlayerLayout = null;
        mPlayerListenerList = new CopyOnWriteArrayList<>();
        mPlayerEventDispatcher = new PlayerEventDispatcher(mPlayerListenerList);
        mPlayerEventDispatcher.init(mPlayer);
        addPlayerListener(mPlayerListener);
        mControllerView = mBuilder.mControllerView;
        setPlayerControllerView(mControllerView);
        mPlayerLayout = mBuilder.mPlayerLayout;
        setPlayerLayout(mPlayerLayout);
        setDataSource(mBuilder.mUrl);
        return this;
    }


    /**
     * 释放播放器
     */
    @UiThread
    public void release() {
        if (mPlayer == null) {
            return;
        }
        mBuilder = null;
        removePlayerControllerView();
        if (mPlayerLayout != null) {
            mPlayerLayout.dettachPlayer();
            mPlayerLayout = null;
        }
        mDisplayModeController.release();
        mDisplayModeController = null;
        mPlayerEventDispatcher.release();
        mPlayerEventDispatcher = null;
        mPlayerListenerList.clear();
        mPlayerListenerList = null;
        mPlayer.release();
        mPlayer = null;
    }

    private void checkPlayer() {
        if (mPlayer == null) {
            throw new IllegalStateException("The player is not init !");
        }
    }

    /**
     * 设置播放路径
     *
     * @param path
     */
    public NikoPlayer setDataSource(String path) {
        checkPlayer();
        if (mDataSource == null) {
            if (mBuilder.mIsUseCache) {
                HttpProxyCacheUtil.getInstance().init(mBuilder.mHttpProxyCacheServerBuilder);
                path = HttpProxyCacheUtil.getInstance().getCacheServer().getProxyUrl(path);
            }
            mPlayer.setDataSource(path);
            mDataSource = path;
        }
        return this;
    }

    /**
     * 设置是否边下边播
     *
     * @param isUseCache true，边下边播
     * @return
     */
    public NikoPlayer setIsUseCache(boolean isUseCache) {
        mBuilder.mIsUseCache = isUseCache;
        return this;
    }

    /**
     * 获取当前播放的状态
     *
     * @return
     */
    public PlayerState getState() {
        checkPlayer();
        return mPlayer.getState();
    }

    /**
     * 开始播放
     */
    @UiThread
    public NikoPlayer start() {
        checkPlayer();
        mPlayer.start();
        return this;
    }

    /**
     * 暂停播放
     */
    @UiThread
    public void pause() {
        checkPlayer();
        mPlayer.pause();
    }

    /**
     * 停止播放
     */
    @UiThread
    public void stop() {
        checkPlayer();
        mPlayer.stop();
    }

    /**
     * 设置播放位置
     *
     * @param timeInUs
     */
    public void seek(long timeInUs) {
        checkPlayer();
        mPlayer.seek(timeInUs);
    }

    /**
     * 获取视频时长
     *
     * @return
     */
    public long getDuration() {
        checkPlayer();
        return mPlayer.getDuration();
    }

    /**
     * 获取播放进度
     *
     * @return
     */
    public long getProgress() {
        checkPlayer();
        return mPlayer.getProgress();
    }

    /**
     * 设置是否循环播放
     *
     * @param isLoop
     */
    public NikoPlayer setLoop(boolean isLoop) {
        checkPlayer();
        mBuilder.mIsLoop = isLoop;
        mPlayer.setLoop(isLoop);
        return this;
    }

    /**
     * 设置播放器渲染载体
     *
     * @param layout
     */
    @UiThread
    public NikoPlayer setPlayerLayout(final BasePlayerLayout layout) {
        checkPlayer();
        mPlayerLayout = layout;
        if (mPlayerLayout != null) {
            mPlayerLayout.attachPlayer(mPlayer);
            mPlayerLayout.setScaleMode(mBuilder.mScaleMode);
            mPlayerLayout.updateImageSize(mVideoWidth, mVideoHeight);
            mDisplayModeController.attachPlayerLayout(layout);
            setDisplayMode(mBuilder.mDisplayMode);
        }
        return this;
    }

    /**
     * 设置音量
     *
     * @param left  左边
     * @param right 右边
     */
    public NikoPlayer setVolume(float left, float right) {
        checkPlayer();
        mPlayer.setVolume(left, right);
        return this;
    }


    /**
     * 获取显示模式
     *
     * @return
     */
    public DisplayMode getDisplayMode() {
        checkPlayer();
        return mDisplayModeController.getDisplayMode();
    }

    /**
     * 设置缩放模式
     *
     * @param mode 默认是Fill
     */
    @UiThread
    public NikoPlayer setScaleMode(ViewScaleUtil.ScaleMode mode) {
        if (mode != null) {
            mBuilder.mScaleMode = mode;
        }
        if (mPlayerLayout != null && mode != null) {
            mPlayerLayout.setScaleMode(mode);
            mPlayerLayout.updateImageSize(mVideoWidth, mVideoHeight);
        }
        return this;
    }

    private SimplePlayerListener mPlayerListener = new SimplePlayerListener() {
        @Override
        public void onVideoSizeChanged(int width, int height) {
            NikoPlayer.this.onVideoSizeChanged(width, height);
        }
    };

    private synchronized void onVideoSizeChanged(int width, int height) {
        if (width != 0) {
            mVideoWidth = width;
        }
        if (height != 0) {
            mVideoHeight = height;
        }
        if (mPlayerLayout != null) {
            mPlayerLayout.updateImageSize(mVideoWidth, mVideoHeight);
        }
    }


    public static class Builder {
        // 边下边播的配置
        private HttpProxyCacheServer.Builder mHttpProxyCacheServerBuilder;
        // 是否边下边播
        private boolean mIsUseCache;
        // 播放器类型
        private IPlayerFactory.PlayerType mPlayerType;
        // 创建播放器的工厂
        private IPlayerFactory mPlayerFactory;
        // 创建小窗的LayoutParams参数工厂
        private ITinyWindowParamFactory mTinyWindowParamFactory;
        // 显示模式
        private DisplayMode mDisplayMode;
        // 是否循环播放
        private boolean mIsLoop;
        // 音量
        private float mVolume = 1.0f;
        // 播放器渲染载体
        private BasePlayerLayout mPlayerLayout;
        // 缩放模式
        private ViewScaleUtil.ScaleMode mScaleMode;
        // 播放器控制View
        private BasePlayerControlView mControllerView;
        // 是否在prepared状态下就开始播
        private boolean mIsStartOnPrepared = true;
        // 遇到错误是否重连
        private boolean mIsReconnectOnError;
        // 遇到错误后重连间隔时间(S)
        private int mReconnectTime = 5;
        // 是否硬解
        private boolean mIsHardDecode = true;
        // 缓冲区大小,默认是500k
        private int mMaxBufferSize = 500 * 1024;
        // 多少帧以后开始播放，默认是100帧
        private int mMinFrames = 100;
        // 播放地址
        private String mUrl;

        public Builder() {
            mPlayerType = IPlayerFactory.PlayerType.ANDROID_MEDIA_PLAYER;
            mPlayerFactory = new PlayerFactoryImpl();
            mTinyWindowParamFactory = new TinyWindowParamFactoryImpl();
            mScaleMode = ViewScaleUtil.ScaleMode.AspectFit;
            mDisplayMode = DisplayMode.PORTRAIT;
            mHttpProxyCacheServerBuilder = new HttpProxyCacheServer.Builder(BaseApplication.getContext());
        }

        /**
         * 设置边下边播的参数配置
         *
         * @param builder
         * @return
         */
        public Builder setHttpProxyCacheServerBuilder(HttpProxyCacheServer.Builder builder) {
            if (builder != null) {
                mHttpProxyCacheServerBuilder = builder;
            }
            return this;
        }

        /**
         * 设置使用哪种播放器，默认是  ANDROID_MEDIA_PLAYER
         *
         * @param type
         * @return
         */
        public Builder setPlayerType(IPlayerFactory.PlayerType type) {
            if (type != null) {
                mPlayerType = type;
            }
            return this;
        }

        /**
         * 设置是否边下边播
         *
         * @param isUseCache
         * @return
         */
        public Builder setIsUseCache(boolean isUseCache) {
            mIsUseCache = isUseCache;
            return this;
        }

        /**
         * 设置重连间隔时间，默认是5s
         *
         * @param timeInSecond 单位是秒
         * @return
         */
        public Builder setReconectTime(int timeInSecond) {
            mReconnectTime = timeInSecond;
            return this;
        }

        /**
         * 设置最小多少帧以后开始播放，目前只有 IJK_PLAYER 支持
         *
         * @param minFrames
         * @return
         */
        public Builder setMinFrames(int minFrames) {
            mMinFrames = minFrames;
            return this;
        }

        /**
         * 设置缓冲区大小，目前只有 IJK_PLAYER 支持
         *
         * @param size
         * @return
         */
        public Builder setMaxBufferSize(int size) {
            mMaxBufferSize = size;
            return this;
        }

        /**
         * 设置是否硬解(默认硬解)，需要播放器支持
         *
         * @param isHardDecode
         * @return
         */
        public Builder setIsHardDecode(boolean isHardDecode) {
            mIsHardDecode = isHardDecode;
            return this;
        }

        /**
         * 设置是否错误后自动重连
         *
         * @param isReconnectOnError
         * @return
         */
        public Builder setIsReconnectOnError(boolean isReconnectOnError) {
            mIsReconnectOnError = isReconnectOnError;
            return this;
        }

        /**
         * 设置是否prepared后自动播放，默认自动播放
         *
         * @param isStartOnPrepared
         * @return
         */
        public Builder setIsStartOnPrepared(boolean isStartOnPrepared) {
            mIsStartOnPrepared = isStartOnPrepared;
            return this;
        }

        /**
         * 设置播放器创建工厂,如果有自定义的播放器，则设置次方法来
         * 创建改播放器
         *
         * @param factory
         * @return
         */
        public Builder setPlayerFactory(IPlayerFactory factory) {
            if (factory != null) {
                mPlayerFactory = factory;
            }
            return this;
        }


        /**
         * 添加播放器控制View
         *
         * @param view
         */
        @UiThread
        public Builder setPlayerControllerView(BasePlayerControlView view) {
            mControllerView = view;
            return this;
        }


        /**
         * 设置播放地址
         *
         * @param url
         * @return
         */
        public Builder setDataSource(String url) {
            mUrl = url;
            return this;
        }


        /**
         * 设置显示模式,默认是 PORTRAIT 模式
         *
         * @param mode
         * @return
         */
        @UiThread
        public Builder setDisplayMode(DisplayMode mode) {
            if (mode != null) {
                mDisplayMode = mode;
            }
            return this;
        }

        /**
         * 设置创建小窗的LayoutParam的工厂，主要用来
         * 设置小窗模式的时候小窗的大小和位置,如果不设置
         * 则小窗模式下是在右下角，屏幕的宽和60%和高的16:9的比例
         *
         * @param factory
         * @return
         */
        public Builder setTinyWindowParamFactory(ITinyWindowParamFactory factory) {
            if (factory != null) {
                mTinyWindowParamFactory = factory;
            }
            return this;
        }

        /**
         * 设置是否循环播放,默认不循环
         *
         * @param isLoop
         */
        public Builder setLoop(boolean isLoop) {
            mIsLoop = isLoop;
            return this;
        }

        /**
         * 设置播放器渲染载体
         *
         * @param layout
         */
        @UiThread
        public Builder setPlayerLayout(BasePlayerLayout layout) {
            mPlayerLayout = layout;
            return this;
        }

        /**
         * 设置音量
         *
         * @param volume 0~1
         */
        public Builder setVolume(float volume) {
            mVolume = volume;
            return this;
        }


        /**
         * 设置缩放模式
         *
         * @param mode 默认是Fill
         */
        @UiThread
        public Builder setScaleMode(ViewScaleUtil.ScaleMode mode) {
            if (mode != null) {
                mScaleMode = mode;
            }
            return this;
        }

        public NikoPlayer create() {
            return new NikoPlayer(this);
        }


    }
}

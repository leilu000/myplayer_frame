package player.base.inter;

import player.bean.DisplayMode;

/**
 * Created by ll on 2019/12/5.
 */
public interface IPlayerListener {

    /**
     * 当显示模式发声边改的时候回调
     *
     * @param orientation
     */
    void onDisplayModeChanged(DisplayMode orientation);

    /**
     * 缓冲的时候回调
     *
     * @param percent
     */
    void onBuffering(int percent);

    /**
     * 首帧渲染的时候回调
     */
    void onFirstRenderStart();

    /**
     * 播放中回调
     */
    void onPlaying();

    /**
     * 播放器资源初始化好回调
     */
    void onPrepared();

    /**
     * 暂停成功回调
     */
    void onPaused();

    /**
     * 从暂停恢复成功回调
     */
    void onResume();

    /**
     * 停止成功回调
     */
    void onStopped();

    /**
     * 出错回调
     *
     * @param msg
     * @param type
     */
    void onError(String msg, int type);

    /**
     * 播放完成回调
     */
    void onComplete();

    /**
     * 开始缓冲回调
     */
    void onBufferingStart();

    /**
     * 缓冲结束回调
     */
    void onBufferingEnd();

    /**
     * 视频宽高发声改变回调
     *
     * @param width
     * @param height
     */
    void onVideoSizeChanged(int width, int height);

}

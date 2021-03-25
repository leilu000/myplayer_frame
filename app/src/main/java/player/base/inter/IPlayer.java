package player.base.inter;

import android.view.Surface;


import player.bean.DisplayMode;
import player.bean.PlayerState;

/**
 * Created by ll on 2019/12/5.
 */
public interface IPlayer {

    /**
     * 获取缓冲进度
     *
     * @return
     */
    int getBufferingPercent();

    /**
     * 设置播放地地址
     *
     * @param path
     */
    void setDataSource(String path);

    /**
     * 开始播放
     */
    void start();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 释放播放器资源
     */
    void release();

    /**
     * 跳到XX毫秒处播放
     *
     * @param timeInMs 单位是毫秒
     */
    void seek(long timeInMs);

    /**
     * 获取时长
     *
     * @return
     */
    long getDuration();

    /**
     * 获取播放器进度
     *
     * @return
     */
    long getProgress();

    /**
     * 设置监听器
     *
     * @param listener
     */
    void setPlayerListener(IPlayerListener listener);

    /**
     * 移除监听器
     *
     * @param listener
     */
    void removePlayerListener(IPlayerListener listener);

    /**
     * 设置surface
     *
     * @param surface
     */
    void setSurfaceView(Surface surface);

    /**
     * 设置释放循环播放
     *
     * @param isLoop
     */
    void setLoop(boolean isLoop);

    /**
     * 设置音量大小
     *
     * @param left
     * @param right
     */
    void setVolume(float left, float right);

    /**
     * 获取当前状态
     *
     * @return
     */
    PlayerState getState();

}

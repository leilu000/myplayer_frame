package player.bean;

/**
 * Created by ll on 2019/12/6.
 */
public class PlayerParam {

    // 是否在prepared状态下就开始播放，默认是
    public boolean mIsStartOnPrepared;
    // 遇到错误是否重连
    public boolean mIsReconnectOnError;
    // 是否循环播放
    public boolean mIsLoop;
    // 是否硬解
    public boolean mIsHardDecode;
    // 缓冲区大小
    public int mMaxBufferSize;
    // 多少帧以后开始播放器
    public int mMinFrames;
    // 遇到错误后重连间隔时间(S)
    public int mReconnectTime;


}

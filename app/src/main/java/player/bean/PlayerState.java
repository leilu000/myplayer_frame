package player.bean;

/**
 * Created by ll on 2019/12/5.
 */
public enum PlayerState {
    IDLE(0),
    ERROR(1),
    STOPPED(2),
    COMPLETE(3),
    PREPARED(4),
    PAUSED(5),
    BUFFERING_START(6),
    BUFFERING_END(7),
    PLAYING(8);

    public int value;

    PlayerState(int value) {
        this.value = value;
    }
}

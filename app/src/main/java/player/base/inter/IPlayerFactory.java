package player.base.inter;


import player.bean.PlayerParam;
import player.manager.PlayerFactoryImpl;

/**
 * 创建播放器的工厂类,可以支持扩展
 * Created by ll on 2019/12/22.
 */
public interface IPlayerFactory {

    enum PlayerType {
        IJK_PLAYER, ANDROID_MEDIA_PLAYER
    }

    IPlayer createPlayer(PlayerFactoryImpl.PlayerType type, PlayerParam playerParam);

}

package player.manager;

import player.base.inter.IPlayer;
import player.base.inter.IPlayerFactory;
import player.bean.PlayerParam;
import player.player.AndroidYomePlayer;
import player.player.IjkYomePlayer;

/**
 * Created by ll on 2019/12/5.
 */
public class PlayerFactoryImpl implements IPlayerFactory {


    @Override
    public IPlayer createPlayer(PlayerType type, PlayerParam playerParam) {
        IPlayer player;
        switch (type) {
            case ANDROID_MEDIA_PLAYER:
                player = new AndroidYomePlayer(playerParam);
                break;
            case IJK_PLAYER:
                player = new IjkYomePlayer(playerParam);
                break;
            default:
                throw new RuntimeException("The " + playerParam + " is not support !");
        }
        return player;
    }

}

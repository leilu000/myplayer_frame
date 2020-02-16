package player.manager;

import player.base.inter.IPlayer;
import player.base.inter.IPlayerFactory;
import player.bean.PlayerParam;
import player.player.AndroidPlayer;
import player.player.IjkPlayer;

/**
 * Created by ll on 2019/12/5.
 */
public class PlayerFactoryImpl implements IPlayerFactory {


    public IPlayer createPlayer(PlayerType type, PlayerParam playerParam) {
        IPlayer player;
        switch (type) {
            case ANDROID_MEDIA_PLAYER:
                player = new AndroidPlayer(playerParam);
                break;
            case IJK_PLAYER:
                player = new IjkPlayer(playerParam);
                break;
            default:
                throw new RuntimeException("The " + playerParam + " is not support !");
        }
        return player;
    }

}

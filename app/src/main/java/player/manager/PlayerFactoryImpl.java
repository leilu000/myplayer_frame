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
    public IPlayer createPlayer(PlayerParam playerParam) {
        return new AndroidYomePlayer(playerParam);
    }

}

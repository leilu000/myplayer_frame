package player.base.inter;


import player.bean.PlayerParam;

/**
 * 创建播放器的工厂类,可以支持扩展
 * Created by ll on 2019/12/22.
 */
public interface IPlayerFactory {

    IPlayer createPlayer(PlayerParam playerParam);

}

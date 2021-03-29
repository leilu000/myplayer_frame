package player.manager;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import player.base.BasePlayerControlView;
import player.base.BaseRenderLayout;
import player.base.inter.IPlayer;
import player.bean.DisplayMode;

/**
 * PlayerFrame
 *
 * <p>Description: </p>
 * <br>
 *
 * <p>Copyright: Copyright (c) 2021</p>
 *
 * @author leilu.lei@alibaba-inc.com
 * @version 1.0
 * 3/25/21 3:44 PM
 */
public class FloatWindowTouchController implements View.OnTouchListener {


    private BasePlayerControlView mPlayerControlView;
    private BaseRenderLayout mRenderLayout;
    private IPlayer mPlayer;

    public FloatWindowTouchController(IPlayer player, BasePlayerControlView playerControlView
            , BaseRenderLayout renderLayout) {
        mPlayer = player;
        mPlayerControlView = playerControlView;
        mRenderLayout = renderLayout;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mPlayer == null) {
            return false;
        }
        if (mRenderLayout.onTouch(v, event)) {
            return true;
        }
        return mPlayerControlView != null && mPlayerControlView.onTouch(v, event);
    }

}

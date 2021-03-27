package player.manager;

import android.view.Gravity;
import android.widget.FrameLayout;

import player.base.inter.ITinyWindowParamFactory;
import player.util.Utils;


/**
 * Created by ll on 2019/12/22.
 */
public class FloatWindowParamFactoryImpl implements ITinyWindowParamFactory {

    @Override
    public FrameLayout.LayoutParams createLayoutParam() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                (int) (Utils.getScreenWidth() * 0.6f),
                (int) (Utils.getScreenWidth() * 0.6f * 9f / 16f));
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.rightMargin = Utils.dip2px(8f);
        params.bottomMargin = Utils.dip2px(8f);
        return params;
    }
}

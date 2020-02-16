package player.base.inter;

import android.widget.FrameLayout;

/**
 * Created by ll on 2019/12/22.
 */
public interface ITinyWindowParamFactory {

    /**
     * 在创建小窗口的时候回调，主要用于设置小窗口的位置
     * 和大小
     *
     * @return
     */
    FrameLayout.LayoutParams createLayoutParam();

}

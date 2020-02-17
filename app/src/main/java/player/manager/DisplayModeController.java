package player.manager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import player.base.BasePlayerControlView;
import player.base.BaseRenderLayout;
import player.base.inter.ITinyWindowParamFactory;
import player.bean.DisplayMode;
import player.util.Utils;

/**
 * 显示模式控制器
 * Created by ll on 2019/12/21.
 */
public class DisplayModeController {
    private DisplayMode mDisplayMode = DisplayMode.PORTRAIT;
    private ContainerInfo mPlayerLayoutContainerInfo;
    private ContainerInfo mPlayerControlViewContainerInfo;
    private ITinyWindowParamFactory mTinyWindowParamFactory;

    public DisplayModeController(ITinyWindowParamFactory tinyWindowParamFactory) {
        mTinyWindowParamFactory = tinyWindowParamFactory;
    }

    public void attachPlayerLayout(BaseRenderLayout layout) {
        mPlayerLayoutContainerInfo = createContainerInfo(layout);
    }

    public void attachPlayerControllerView(BasePlayerControlView view) {
        mPlayerControlViewContainerInfo = createContainerInfo(view);
    }

    private ContainerInfo createContainerInfo(View childView) {
        ContainerInfo info = new ContainerInfo();
        info.childView = childView;
        info.parentView = (ViewGroup) childView.getParent();
        info.layoutParams = childView.getLayoutParams();
        info.index = Utils.getChildIndexOfViewGroup(info.parentView, childView);
        return info;
    }

    public void detachPlayerControllerView() {
        mPlayerControlViewContainerInfo = null;
    }

    // 设置显示模式
    public boolean setDisplayMode(DisplayMode mode) {
        if (mode == null) {
            Log.i("leilu", "setDisplayMode mode:" + mode + "   mDisplayMode:" + mDisplayMode);
            return false;
        }
        if (mode == DisplayMode.LANDSCAPE_FULL_SCREEN) {
            enterLandscape(mode);
        } else if (mode == DisplayMode.PORTRAIT_FULL_SCREEN) {
            enterPortraitFullScreen(mode);
        } else if (mode == DisplayMode.PORTRAIT) {
            enterPortrait(mode);
        } else if (mode == DisplayMode.INNER_ACTIVITY_TINY_WINDOW) {
            enterActivityTinyWindow(mode);
        }
        return true;
    }

    // 开启竖屏全屏
    private void enterPortraitFullScreen(DisplayMode mode) {
        if (mPlayerLayoutContainerInfo != null) {
            changeToPortraitFullScreen(mPlayerLayoutContainerInfo, mode);
        }
        if (mPlayerControlViewContainerInfo != null) {
            changeToPortraitFullScreen(mPlayerControlViewContainerInfo, mode);
        }
    }

    // 开启Activity内小窗模式
    private void enterActivityTinyWindow(DisplayMode mode) {
        if (mPlayerLayoutContainerInfo != null) {
            changeToTinyWindow(mPlayerLayoutContainerInfo, mode);
        }
        if (mPlayerControlViewContainerInfo != null) {
            changeToTinyWindow(mPlayerControlViewContainerInfo, mode);
        }
    }

    // 开启横屏
    private void enterLandscape(DisplayMode mode) {
        if (mPlayerLayoutContainerInfo != null) {
            changeToLandscape(mPlayerLayoutContainerInfo, mode);
        }
        if (mPlayerControlViewContainerInfo != null) {
            changeToLandscape(mPlayerControlViewContainerInfo, mode);
        }
    }

    // 开启竖屏
    private void enterPortrait(DisplayMode mode) {
        if (mPlayerLayoutContainerInfo != null) {
            changeToPortrait(mPlayerLayoutContainerInfo, mode);
        }
        if (mPlayerControlViewContainerInfo != null) {
            changeToPortrait(mPlayerControlViewContainerInfo, mode);
        }
    }

    private void changeToTinyWindow(ContainerInfo info, DisplayMode mode) {
        if (info.mode != mode) {
            mDisplayMode = info.mode = mode;
            Context context = info.childView.getContext();
            Activity activity = Utils.getActivityForContext(context);
            ViewGroup contentView = activity.findViewById(android.R.id.content);
            info.parentView.removeView(info.childView);
            FrameLayout.LayoutParams params = mTinyWindowParamFactory.createLayoutParam();
            contentView.addView(info.childView, params);
        }
    }

    private void changeToLandscape(ContainerInfo info, DisplayMode mode) {
        if (info.mode != mode) {
            mDisplayMode = info.mode = mode;
            Context context = info.parentView.getContext();
            Utils.hideActionBar(context);
            Activity activity = Utils.getActivityForContext(context);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            ViewGroup contentView = activity.findViewById(android.R.id.content);
            ((ViewGroup) info.childView.getParent()).removeView(info.childView);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            contentView.addView(info.childView, params);
        }
    }

    private void changeToPortrait(ContainerInfo info, DisplayMode mode) {
        if (info.mode != mode) {
            mDisplayMode = info.mode = mode;
            Context context = info.parentView.getContext();
            Activity activity = Utils.getActivityForContext(context);
            Utils.showActionBar(context);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ((ViewGroup) info.childView.getParent()).removeView(info.childView);
            info.parentView.addView(info.childView, info.index, info.layoutParams);
        }
    }


    private void changeToPortraitFullScreen(ContainerInfo info, DisplayMode mode) {
        if (info.mode != mode) {
            mDisplayMode = info.mode = mode;
            Context context = info.parentView.getContext();
            Activity activity = Utils.getActivityForContext(context);
            Utils.hideActionBar(context);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ((ViewGroup) info.childView.getParent()).removeView(info.childView);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            ViewGroup contentView = activity.findViewById(android.R.id.content);
            contentView.addView(info.childView, params);
        }
    }

    public void release() {
        mPlayerLayoutContainerInfo = null;
        mPlayerControlViewContainerInfo = null;
    }

    public DisplayMode getDisplayMode() {
        return mDisplayMode;
    }

    private class ContainerInfo {
        View childView;
        ViewGroup parentView;
        ViewGroup.LayoutParams layoutParams;
        int index;
        DisplayMode mode;
    }
}

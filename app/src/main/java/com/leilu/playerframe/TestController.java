package com.leilu.playerframe;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import player.base.BasePlayerControlView;
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
 * 3/25/21 2:12 PM
 */
public class TestController extends BasePlayerControlView {


    public TestController(@NonNull Context context) {
        super(context);
    }

    public TestController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void showControllerView() {

    }

    @Override
    protected void hideControllerView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected int getControlViewLayoutId() {
        return 0;
    }

    @Override
    protected boolean isNeedStartProgressTimer() {
        return false;
    }

    @Override
    protected void onProgressChanged(long duration, long progress) {

    }

    @Override
    protected void onError(String msg) {

    }

    @Override
    protected void onComplete() {

    }

    @Override
    protected void onPaused() {

    }

    @Override
    protected void onBufferingEnd() {

    }

    @Override
    protected void onBufferingStart() {

    }

    @Override
    protected void onPlaying(long duration, long progress) {

    }

    @Override
    protected void onDisplayModeChanged(DisplayMode orientation) {

    }

    @Override
    protected void onBrightnessChangeStart() {

    }

    @Override
    protected void onBrightnessChangeEnd() {

    }

    @Override
    protected void onVolumeChangeStart() {

    }

    @Override
    protected void onVolumeChangeEnd() {

    }

    @Override
    protected boolean isSupportBrightness() {
        return false;
    }

    @Override
    protected boolean isSupportVolume() {
        return false;
    }

    @Override
    protected boolean isSupportSeek() {
        return false;
    }

    @Override
    protected void onSeekStart() {

    }

    @Override
    protected void onSeekEnd() {

    }

    @Override
    protected void onBrightnessChanged(int currentBrightness, int maxBrightness) {

    }

    @Override
    protected void onVolumeChanged(float value, int maxVolume) {

    }
}

package player.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.leilu.playerframe.R;

import player.base.BasePlayerControlView;
import player.bean.DisplayMode;
import player.util.TimeUtil;


/**
 * 自带的播放器控制试图,如果需要创建自己的UI试图，则参考此类继承BasePlayerControlView即可
 */
public class PlayerControlView extends BasePlayerControlView implements View.OnClickListener {

    private ImageView mIvPlay;
    private ImageView mIvLoading;
    private ImageView mIvScreenOrientation;
    private SeekBar mSbProgress;
    private TextView mTvDuration;
    private TextView mTvTime;
    private LinearLayout mLLBottomPanel;

    private View mCenterGroupView;
    private ImageView mIvIcon;
    private SeekBar mSbBrightnessProgress;
    private TextView mTvBrightnessProgress;

    public PlayerControlView(@NonNull Context context) {
        this(context, null);
    }

    public PlayerControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public PlayerControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getControlViewLayoutId() {
        return R.layout.view_player_control;
    }

    @Override
    protected boolean isNeedStartProgressTimer() {
        return true;
    }

    @Override
    protected void onProgressChanged(long duration, long progress) {
        mSbProgress.setMax((int) duration);
        mSbProgress.setProgress((int) progress);

    }

    @Override
    protected void onError(String msg) {
        mIvLoading.setVisibility(View.GONE);
        onPaused();
    }

    @Override
    protected void onComplete() {
        onPaused();
    }

    @Override
    protected void onSeekStart() {

    }

    @Override
    protected void onSeekEnd() {

    }

    @Override
    protected void onPlaying(long duration, long progress) {
        mIvLoading.setVisibility(View.GONE);
        mSbProgress.setMax((int) duration);
        mSbProgress.setProgress((int) progress);
        mIvPlay.setImageResource(R.drawable.bg_video_controller_pause);
        mTvDuration.setText(TimeUtil.formatIsMS(duration));
        mTvTime.setText(TimeUtil.formatIsMS(progress));
    }

    @Override
    protected void onPaused() {
        mIvPlay.setImageResource(R.drawable.bg_video_controller_play);
    }

    @Override
    protected void onBufferingEnd() {
        mIvPlay.setVisibility(View.VISIBLE);
        mIvLoading.setVisibility(View.GONE);
    }

    @Override
    protected void onDisplayModeChanged(DisplayMode orientation) {
        if (isLandscape()) {
            mIvScreenOrientation.setImageResource(R.drawable.bg_video_controller_halfscreen);
        } else {
            mIvScreenOrientation.setImageResource(R.drawable.bg_video_controller_fullscreen);
        }
    }

    @Override
    protected void onBufferingStart() {
        mIvPlay.setVisibility(View.GONE);
        mIvLoading.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onBrightnessChanged(int currentBrightness, int maxBrightness) {
        mSbBrightnessProgress.setMax(maxBrightness);
        mSbBrightnessProgress.setProgress(currentBrightness);
        mIvIcon.setImageResource(R.drawable.bg_video_controller_brightness);
        int radio = currentBrightness * 100 / maxBrightness;
        mTvBrightnessProgress.setText(radio + "%");
    }

    @Override
    protected void onVolumeChanged(float value, int maxVolume) {
        mSbBrightnessProgress.setMax(maxVolume);
        mSbBrightnessProgress.setProgress((int) value);
        mIvIcon.setImageResource(R.drawable.bg_video_controller_volume);
        int radio = (int) (value * 100 / maxVolume);
        mTvBrightnessProgress.setText(radio + "%");
    }

    @Override
    protected void initListener() {
        mIvPlay.setOnClickListener(this);
        mIvScreenOrientation.setOnClickListener(this);
        mSbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTvTime.setText(TimeUtil.formatIsMS((long) progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seek(seekBar.getProgress());
            }
        });
    }

    @Override
    protected void initView() {
        mIvPlay = findViewById(R.id.iv_play);
        mIvLoading = findViewById(R.id.iv_loading);
        mSbProgress = findViewById(R.id.sb_progress);
        mTvDuration = findViewById(R.id.tv_duration);
        mTvTime = findViewById(R.id.tv_time);
        mIvScreenOrientation = findViewById(R.id.iv_screen_orientation);
        mCenterGroupView = findViewById(R.id.center_group_view);
        mIvIcon = findViewById(R.id.iv_icon);
        mSbBrightnessProgress = findViewById(R.id.sb_brightness_progress);
        mTvBrightnessProgress = findViewById(R.id.tv_brightness_progress);
        mLLBottomPanel = findViewById(R.id.ll_bottom_panel);
    }

    @Override
    public void onClick(View v) {
        if (v == mIvPlay) {
            togglePlay();
        } else if (v == mIvScreenOrientation) {
            if (isLandscape()) {
                setDisplayMode(DisplayMode.PORTRAIT);
            } else {
                setDisplayMode(DisplayMode.LANDSCAPE_FULL_SCREEN);
            }
        }
    }

    @Override
    protected void onBrightnessChangeStart() {
        mCenterGroupView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onBrightnessChangeEnd() {
        mCenterGroupView.setVisibility(View.GONE);
    }

    @Override
    protected void onVolumeChangeStart() {
        mCenterGroupView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onVolumeChangeEnd() {
        mCenterGroupView.setVisibility(View.GONE);
    }

    @Override
    protected void showControllerView() {
        mLLBottomPanel.setVisibility(View.VISIBLE);
    }

    @Override
    protected void hideControllerView() {
        mLLBottomPanel.setVisibility(View.GONE);
    }

    @Override
    protected boolean isSupportBrightness() {
        return true;
    }

    @Override
    protected boolean isSupportVolume() {
        return true;
    }

    @Override
    protected boolean isSupportSeek() {
        return true;
    }
}

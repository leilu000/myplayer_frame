package com.leilu.playerframe;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import player.base.inter.IPlayerFactory;
import player.base.inter.OnTinyWindowClickListener;
import player.bean.DisplayMode;
import player.bean.SimplePlayerListener;
import player.manager.YomePlayer;
import player.util.ViewScaleUtil;
import player.view.PlayerControlView;
import player.view.TextureRenderLayout;

public class MainActivity extends AppCompatActivity {

    private static final String URL = "http://1259438468.vod2.myqcloud.com/6ac6c9d4vodcq1259438468/2dc46ae95285890798568929916/Grvg1EX8yAQA.mp4";

    private YomePlayer mPlayer;
    private TextureRenderLayout mTexturePlayerLayout;
    private PlayerControlView mControlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        mTexturePlayerLayout = findViewById(R.id.layout);
        mControlView = findViewById(R.id.pcv);
        initPlayer();
    }

    private void initPlayer() {
        mPlayer = new YomePlayer.Builder()
                // 设置显示模式(横屏、竖屏、竖屏全屏、悬浮窗)
                .setDisplayMode(DisplayMode.PORTRAIT)
                // 是否使用硬件解码（默认就是硬件解码）
                .setIsHardDecode(true)
                // 设置是否自动播放
                .setIsStartOnPrepared(true)
                // 设置渲染载体
                .setRenderLayout(mTexturePlayerLayout)
                // 设置播放器控制view
                .setPlayerControllerView(mControlView)
                // 是否循环播放
                .setLoop(true)
                // 设置是否保存悬浮窗的拖动位置，如果为true下次显示位于上次的位置
                .setSaveTinyWindowPosition(true)
                // 是否边下边播
                .setIsUseCache(true)
                // 设置播放器内核类型（）
                .setPlayerType(IPlayerFactory.PlayerType.IJK_PLAYER)
                // 设置创建播放器的工厂，用于创建自己想要的播放器内核
//                .setPlayerFactory(new IPlayerFactory() {
//                    @Override
//                    public IPlayer createPlayer(PlayerType type, PlayerParam playerParam) {
//                        return new IjkYomePlayer(playerParam);
//                    }
//                })
                // 设置播放地址
                .setDataSource(URL)
                // 设置缩放模式
                .setScaleMode(ViewScaleUtil.ScaleMode.AspectFit)
                // 设置悬浮窗点击事件
                .setTinyWindowClickListener(new OnTinyWindowClickListener() {
                    @Override
                    public void onSingleClick() {
                        Log.i("==", "点击了");
                    }

                    @Override
                    public void onDoubleClick() {
                        Log.i("==", "双击了");
                    }
                })
                .create();
        // 添加播放器状态监听
        mPlayer.addPlayerListener(new SimplePlayerListener() {
            @Override
            public void onError(String msg, int type) {
                Log.i("==", "onError:" + msg);
            }

            @Override
            public void onFirstRenderStart() {
                Log.i("==", "onFirstRenderStart");
            }

            @Override
            public void onDisplayModeChanged(DisplayMode orientation) {
                Log.i("==", "onDisplayModeChanged:" + orientation);
            }

            @Override
            public void onComplete() {
                Log.i("==", "onComplete");
            }
        });
    }


    public void horizontalScreen(View view) {
        mPlayer.setDisplayMode(DisplayMode.LANDSCAPE_FULL_SCREEN);
    }

    public void portraitFullScreen(View view) {
        mPlayer.setDisplayMode(DisplayMode.PORTRAIT_FULL_SCREEN);
    }

    public void changeWindow(View view) {
        mPlayer.setDisplayMode(DisplayMode.INNER_ACTIVITY_TINY_WINDOW);
    }

    public void fill(View view) {
        mPlayer.setScaleMode(ViewScaleUtil.ScaleMode.Fill);
    }

    public void aspectFit(View view) {
        mPlayer.setScaleMode(ViewScaleUtil.ScaleMode.AspectFit);
    }

    public void centerCrop(View view) {
        mPlayer.setScaleMode(ViewScaleUtil.ScaleMode.CenterCrop);
    }

    @Override
    public void onBackPressed() {
        if (mPlayer.onBackPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.release();
    }

    public void capture(View view) {
        final ImageView iv = findViewById(R.id.iv_capture);
        iv.setImageBitmap(mPlayer.capture());
    }


}

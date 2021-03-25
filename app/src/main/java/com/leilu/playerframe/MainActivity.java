package com.leilu.playerframe;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import player.base.inter.IPlayer;
import player.base.inter.IPlayerFactory;
import player.bean.DisplayMode;
import player.bean.PlayerParam;
import player.bean.SimplePlayerListener;
import player.manager.NikoPlayer;
import player.util.ViewScaleUtil;
import player.view.PlayerControlView;
import player.view.TextureRenderLayout;

public class MainActivity extends AppCompatActivity {

    private NikoPlayer mPlayer;
    private TextureRenderLayout mTexturePlayerLayout;
    private PlayerControlView pcv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTexturePlayerLayout = findViewById(R.id.layout);
        pcv = findViewById(R.id.pcv);
        initPlayer();
//        mTexturePlayerLayout.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                initPlayer();
//            }
//        }, 2000);
    }

    private void initPlayer() {
        String url = "http://1259438468.vod2.myqcloud.com/6ac6c9d4vodcq1259438468/2dc46ae95285890798568929916/Grvg1EX8yAQA.mp4";
        // url = "/sdcard/DCIM/Camera/VID_20200217_150849.mp4";// 横屏拍摄
        // url = "/sdcard/DCIM/Camera/VID_20200217_151125.mp4";// 竖屏拍摄
        mPlayer = new NikoPlayer.Builder()
                .setDisplayMode(DisplayMode.PORTRAIT)
                .setIsHardDecode(true)
                .setIsStartOnPrepared(true)
                .setRenderLayout(mTexturePlayerLayout)
                .setLoop(true)
                .setIsUseCache(true)
                .setPlayerType(IPlayerFactory.PlayerType.IJK_PLAYER)
                .setDataSource(url)
                .setPlayerControllerView(pcv)
                .setScaleMode(ViewScaleUtil.ScaleMode.AspectFit)
                .create();
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


    public void changeOrientation(View view) {
        Button btn = (Button) view;
        mPlayer.setDisplayMode(DisplayMode.LANDSCAPE_FULL_SCREEN);
    }

    public void changeWindow(View view) {
        Button btn = (Button) view;
        mPlayer.setDisplayMode(DisplayMode.INNER_ACTIVITY_TINY_WINDOW);
    }

    public void changeScale(View view) {
        Button btn = (Button) view;
        mPlayer.setScaleMode(ViewScaleUtil.ScaleMode.Fill);
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
        final ImageView iv = findViewById(R.id.iv);
        iv.setImageBitmap(mPlayer.capture());
    }

    public void render(View view) {
        mPlayer.setPlayerLayout(mTexturePlayerLayout);
    }
}

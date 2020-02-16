package com.leilu.playerframe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import player.base.BasePlayer;
import player.base.inter.IPlayerFactory;
import player.bean.DisplayMode;
import player.bean.SimplePlayerListener;
import player.manager.NikoPlayer;
import player.util.ViewScaleUtil;
import player.view.TexturePlayerLayout;

public class MainActivity extends AppCompatActivity {

    private NikoPlayer mPlayer;
    private TexturePlayerLayout mTexturePlayerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTexturePlayerLayout = findViewById(R.id.layout);


        mTexturePlayerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                initPlayer();
            }
        }, 2000);
    }

    private void initPlayer() {
        mPlayer = new NikoPlayer.Builder()
                .setDisplayMode(DisplayMode.PORTRAIT_FULL_SCREEN)
                .setIsHardDecode(true)
                .setIsStartOnPrepared(true)
                .setPlayerLayout(mTexturePlayerLayout)
                .setLoop(true)
                .setPlayerType(IPlayerFactory.PlayerType.IJK_PLAYER)
                .setScaleMode(ViewScaleUtil.ScaleMode.Fill)
                .create();
        mPlayer.setDataSource("http://1259438468.vod2.myqcloud.com/6ac6c9d4vodcq1259438468/2dc46ae95285890798568929916/Grvg1EX8yAQA.mp4");
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
        mPlayer.setScaleMode(ViewScaleUtil.ScaleMode.AspectFit);
    }

    @Override
    public void onBackPressed() {

        if (mPlayer.onBackPress()) {
            return;
        }
        super.onBackPressed();

    }
}

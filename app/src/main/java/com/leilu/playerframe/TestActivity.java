package com.leilu.playerframe;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import player.util.TinyWindowMoveHelper;
import player.util.Utils;

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
 * 3/25/21 5:01 PM
 */
public class TestActivity extends AppCompatActivity implements View.OnTouchListener {

    View view;
    TinyWindowMoveHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        getSupportActionBar().hide();
        final ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                (int) (Utils.getScreenWidth() * 0.6f),
                (int) (Utils.getScreenWidth() * 0.6f * 9f / 16f));
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.rightMargin = Utils.dip2px(8f);
        params.bottomMargin = Utils.dip2px(8f);

        view = new View(this);
        view.setBackgroundColor(Color.BLUE);
        viewGroup.addView(view, params);
        view.setOnTouchListener(this);

        int screenWidth = Utils.getScreenWidth();
        int screenHeight = Utils.getScreenHeight() + Utils.getStatusBarHeight();
        helper = new TinyWindowMoveHelper(screenWidth, screenHeight, null);
//        mScreenWidth = Utils.getScreenWidth();
//        mScreenHeight = Utils.getScreenHeight();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return helper.onTouch(v, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.clear();
    }

    public void changeWindow(View view) {
    }
}

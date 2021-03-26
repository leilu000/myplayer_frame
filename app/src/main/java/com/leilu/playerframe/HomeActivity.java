package com.leilu.playerframe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.imuxuan.floatingview.FloatingMagnetView;
import com.imuxuan.floatingview.FloatingView;

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
 * 3/26/21 10:23 AM
 */
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FloatingView.get().attach(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FloatingView.get().detach(this);
    }

    public void main(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void test(View view) {
        startActivity(new Intent(this, TestActivity.class));
    }

    public void float_window(View v) {
        FloatingView.get().add();

    }
}

package com.leilu.playerframe;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class BaseApplication extends Application {

    private static Context mContext;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}

package com.volley.client;

import android.app.Application;
import android.content.Context;

import com.android.volley.center.AsyncRequestCenter;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public class MyApplication extends Application {

    private static MyApplication instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initHttpLoader(this);
    }

    private void initHttpLoader(Context context) {
        AsyncRequestCenter.getInstance().initiate(context);
    }

    public static MyApplication getInstance() {
        return instance;
    }
}

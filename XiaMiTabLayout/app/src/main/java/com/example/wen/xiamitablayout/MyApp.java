package com.example.wen.xiamitablayout;

import android.app.Application;
import android.content.Context;

/**
 * Created by zhangxiaowen on 2018/5/14.
 */

public class MyApp extends Application {

    public static Context context = null;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
    }
}

package com.hzy.wan;

import com.hzy.baselib.base.BaseApp;
import com.squareup.leakcanary.LeakCanary;

public class App extends BaseApp {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}

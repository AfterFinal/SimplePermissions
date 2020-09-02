package com.lnvip.android.permissions;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Permissions.init(this);
    }
}
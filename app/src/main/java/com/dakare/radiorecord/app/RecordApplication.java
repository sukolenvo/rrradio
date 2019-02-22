package com.dakare.radiorecord.app;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

public class RecordApplication extends Application {
    private static RecordApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static RecordApplication getInstance() {
        return app;
    }
}

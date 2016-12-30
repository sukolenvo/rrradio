package com.dakare.radiorecord.app;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import io.fabric.sdk.android.Fabric;

public class RecordApplication extends Application {
    private static RecordApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this).build());
        app = this;
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics.Builder().answers(new Answers()).core(new CrashlyticsCore()).build());
        }
    }

    public static RecordApplication getInstance() {
        return app;
    }
}

package com.dakare.radiorecord.app;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.ads.MobileAds;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import io.fabric.sdk.android.Fabric;

public class RecordApplication extends Application {
    private static RecordApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                .threadPoolSize(2)
                .build());
        app = this;
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics.Builder().answers(new Answers()).core(new CrashlyticsCore()).build());
        }
        try {
            MobileAds.initialize(getApplicationContext(), "ca-app-pub-8716939423875482~8319018853");
        } catch (RuntimeException e) {
            Crashlytics.logException(e);
        }
    }

    public static RecordApplication getInstance() {
        return app;
    }
}

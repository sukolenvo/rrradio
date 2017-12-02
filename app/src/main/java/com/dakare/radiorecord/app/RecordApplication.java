package com.dakare.radiorecord.app;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.ads.MobileAds;
import io.fabric.sdk.android.Fabric;

public class RecordApplication extends Application {
    private static RecordApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Fabric.with(this, new Crashlytics.Builder()
                .answers(new Answers())
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());
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

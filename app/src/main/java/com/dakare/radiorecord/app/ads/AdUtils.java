package com.dakare.radiorecord.app.ads;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.RecordApplication;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.concurrent.TimeUnit;

public class AdUtils {

    private static long AD_FREE_PERIOD;

    static {
        try {
            AD_FREE_PERIOD = RecordApplication.getInstance()
                    .getPackageManager()
                    .getPackageInfo(RecordApplication.getInstance().getPackageName(), 0)
                    .firstInstallTime + TimeUnit.DAYS.toMillis(3);
        } catch (PackageManager.NameNotFoundException e) {
            //Nothing to do
        }
    }

    public static void showAd(final AdView adView) {
        if (AD_FREE_PERIOD < System.currentTimeMillis()
                && PreferenceManager.getInstance(adView.getContext()).getShowAd()
                && !isMobileConnection(adView.getContext())) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("9740472D383B4180A2C2E09C23C160FC")
                    .build();
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private static boolean isMobileConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.getType() != ConnectivityManager.TYPE_WIFI;
    }
}

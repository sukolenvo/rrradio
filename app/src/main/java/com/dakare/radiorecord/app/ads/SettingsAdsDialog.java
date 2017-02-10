package com.dakare.radiorecord.app.ads;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.dakare.radiorecord.app.AbstractDialog;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.settings.SettingsActivity;

public class SettingsAdsDialog extends AbstractDialog {

    public SettingsAdsDialog(final Context context) {
        super(context);
        setContentView(R.layout.dialog_ads);
        findViewById(R.id.enable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getInstance(context).setShowAd(true);
                dismiss();
            }
        });
        findViewById(R.id.disable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getInstance(context).setShowAd(false);
                dismiss();
            }
        });
    }
}

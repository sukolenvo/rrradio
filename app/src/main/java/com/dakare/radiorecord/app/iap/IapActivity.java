package com.dakare.radiorecord.app.iap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.view.theme.Theme;
import com.dakare.radiorecord.app.view.theme.ThemeActivity;

public class IapActivity extends ThemeActivity implements IapHelper.IapCallback, View.OnClickListener {
    private View smallPaymantButton;
    private View mediumPaymentButton;
    private View largePaymentButton;
    private IapHelper iapHelper;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iap);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        smallPaymantButton = findViewById(R.id.button_iap_small);
        mediumPaymentButton = findViewById(R.id.button_iap_medium);
        largePaymentButton = findViewById(R.id.button_iap_large);
        smallPaymantButton.setOnClickListener(this);
        mediumPaymentButton.setOnClickListener(this);
        largePaymentButton.setOnClickListener(this);
        iapHelper = new IapHelper(this, this);
    }

    @Override
    public void onConnected() {
        smallPaymantButton.setEnabled(true);
        mediumPaymentButton.setEnabled(true);
        largePaymentButton.setEnabled(true);
    }


    @Override
    public void onDisconnected() {
        smallPaymantButton.setEnabled(false);
        mediumPaymentButton.setEnabled(false);
        largePaymentButton.setEnabled(false);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.button_iap_small:
                iapHelper.purchase("donate_1");
                break;
            case R.id.button_iap_medium:
                iapHelper.purchase("donate_5");
                break;
            case R.id.button_iap_large:
                iapHelper.purchase("donate_25");
                break;
            default:
                Log.w("IapActivity", "Unknown button id");
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (!iapHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected int getThemeId(final Theme theme) {
        return theme == Theme.DARK ? R.style.MainDark_Iap : R.style.Main_Iap;
    }
}

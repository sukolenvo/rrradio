package com.dakare.radiorecord.app.iap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.dakare.radiorecord.app.MenuActivity;
import com.dakare.radiorecord.app.R;

public class IapActivity extends MenuActivity implements IapHelper.IapCallback, View.OnClickListener
{
    private View smallPaymantButton;
    private View mediumPaymentButton;
    private View largePaymentButton;
    private IapHelper iapHelper;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iap);
        initToolbar();
        smallPaymantButton = findViewById(R.id.button_iap_small);
        mediumPaymentButton = findViewById(R.id.button_iap_medium);
        largePaymentButton = findViewById(R.id.button_iap_large);
        smallPaymantButton.setOnClickListener(this);
        mediumPaymentButton.setOnClickListener(this);
        largePaymentButton.setOnClickListener(this);
        iapHelper = new IapHelper(this, this);
    }

    @Override
    protected int getMenuContainer()
    {
        return R.id.menu_iap_container;
    }

    @Override
    public void onConnected()
    {
        smallPaymantButton.setEnabled(true);
        mediumPaymentButton.setEnabled(true);
        largePaymentButton.setEnabled(true);
    }


    @Override
    public void onDisconnected()
    {
        smallPaymantButton.setEnabled(false);
        mediumPaymentButton.setEnabled(false);
        largePaymentButton.setEnabled(false);
    }

    @Override
    public void onClick(final View v)
    {
        switch (v.getId())
        {
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
}

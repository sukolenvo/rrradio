package com.dakare.radiorecord.app.iap;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.*;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import com.android.vending.billing.IInAppBillingService;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.PurchaseEvent;
import com.dakare.radiorecord.app.R;
import org.json.JSONException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

public class IapHelper implements ServiceConnection {
    private static final String KEY = "ADIwYjcaDy8p6nPIad591XEf7qHVlnGIg0eOaPfAQCmjo144m2Q1bE2tBcEEkNATONVZQnGfksh0h86OSKvCZfF56solTZ9KyJyOUXwT96X8fgqyZlnjOvyLjluM7FOMIZ7eSna6Rp4zSDsH/1Vw2JnnHoFshIWAOiCCNJrOnQMN/ylEVnDhd7raE2BDVYHlPK9F2mh+tRVUdOnZBXhzcXfDotUVpV8wFHwSPEULeg/jb6HvwrR/zmDb3K11Sh5aKiZCaS9gGusfFwYR1i0nynlKI+M8lc/HHKkW1y1o5JXf8nh9N6KsCzMWSWVVbRpfG7iY2g6MDVySYlpCwsSC6EnUkAEQACKgCBIIMA8QACOAAFEQAB0w9GikhqkgBNAjIBIIMBAQ";
    private static final String ITEM_TYPE_INAPP = "inapp";
    private static final String ITEM_TYPE_SUBSCRIBTION = "subs";
    private static final int BILLING_RESPONSE_RESULT_OK = 0;
    private static final String RESPONSE_CODE = "RESPONSE_CODE";
    private static final String RESPONSE_BUY_INTENT = "BUY_INTENT";
    private static final String RESPONSE_INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    private static final String RESPONSE_INAPP_SIGNATURE = "INAPP_DATA_SIGNATURE";
    public static final String RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";

    private final Activity activity;
    private final IapCallback iapCallback;
    private boolean connected = false;
    private IInAppBillingService mService;

    public IapHelper(final Activity activity, final IapCallback iapCallback) {
        this.activity = activity;
        this.iapCallback = iapCallback;
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        List<ResolveInfo> intentServices = activity.getPackageManager().queryIntentServices(serviceIntent, 0);
        if (intentServices != null && !intentServices.isEmpty()) {
            activity.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
        } else {
            Toast.makeText(activity, R.string.erro_no_iap_service, Toast.LENGTH_LONG).show();
        }
    }

    private String getKey() {
        char[] chars = KEY.toCharArray();
        char[] buffer = Arrays.copyOfRange(chars, chars.length - 7, chars.length);
        System.arraycopy(chars, 0, chars, 7, chars.length - 7);
        System.arraycopy(buffer, 0, chars, 0, 7);
        char[] newChars = new char[chars.length];
        for (int i = 0; i < chars.length; i++) {
            newChars[i] = chars[chars.length - i - 1];
        }
        chars = newChars;
        buffer = Arrays.copyOfRange(chars, chars.length - 4, chars.length);
        System.arraycopy(chars, 0, chars, 4, chars.length - 4);
        System.arraycopy(buffer, 0, chars, 0, 4);
        return new String(chars);
    }

    public void purchase(final String sku, final boolean subscription) {
        if (connected) {
            try {
                Bundle buyIntentBundle = mService.getBuyIntent(3, activity.getPackageName(), sku,
                        subscription ? ITEM_TYPE_SUBSCRIBTION : ITEM_TYPE_INAPP, "");
                int response = getResponseCodeFromBundle(buyIntentBundle);
                if (response != BILLING_RESPONSE_RESULT_OK) {
                    try {
                        Bundle purchaseResponse = mService.getPurchases(3, activity.getPackageName(), ITEM_TYPE_INAPP, null);
                        if (getResponseCodeFromBundle(purchaseResponse) == BILLING_RESPONSE_RESULT_OK) {
                            for (String purchase : purchaseResponse.getStringArrayList(RESPONSE_INAPP_PURCHASE_DATA_LIST)) {
                                mService.consumePurchase(3, activity.getPackageName(), new Purchase(purchase, null).getMToken());
                            }
                        }
                    } catch (RemoteException | JSONException e) {
                        Log.e("IapHelper", "Remote error", e);
                    }
                    Toast.makeText(activity, R.string.error_iap_purchase, Toast.LENGTH_LONG).show();
                    return;
                }
                PendingIntent pendingIntent = buyIntentBundle.getParcelable(RESPONSE_BUY_INTENT);
                try {
                    activity.startIntentSenderForResult(pendingIntent.getIntentSender(),
                            1, new Intent(),
                            Integer.valueOf(0), Integer.valueOf(0),
                            Integer.valueOf(0));
                } catch (IntentSender.SendIntentException e) {
                    Toast.makeText(activity, R.string.error_iap_open, Toast.LENGTH_LONG).show();
                }
            } catch (RemoteException e) {
                Toast.makeText(activity, R.string.error_iap_remote, Toast.LENGTH_LONG).show();
            }
        }
    }

    private int getResponseCodeFromBundle(final Bundle b) {
        Object o = b.get(RESPONSE_CODE);
        if (o == null) {
            return BILLING_RESPONSE_RESULT_OK;
        }
        if (o instanceof Integer) {
            return ((Integer) o).intValue();
        }
        if (o instanceof Long) {
            return (int) ((Long) o).longValue();
        }
        return -1;
    }

    public boolean handleActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode != 1) {
            return false;
        }
        if (data == null) {
            return true;
        }

        int responseCode = getResponseCodeFromBundle(data.getExtras());
        String purchaseData = data.getStringExtra(RESPONSE_INAPP_PURCHASE_DATA);
        String dataSignature = data.getStringExtra(RESPONSE_INAPP_SIGNATURE);

        if (resultCode == Activity.RESULT_OK && responseCode == BILLING_RESPONSE_RESULT_OK) {
            if (purchaseData == null || dataSignature == null || !Security.verifyPurchase(getKey(), purchaseData, dataSignature)) {
                return true;
            }
            savePurchase(purchaseData, dataSignature);
            Toast.makeText(activity, R.string.iap_thanks, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    private void savePurchase(final String purchaseData, final String dataSignature) {
        try {
        Purchase purchase = new Purchase(purchaseData, dataSignature);
        Answers.getInstance().logPurchase(new PurchaseEvent()
                .putItemPrice(BigDecimal.valueOf(getPurchaseValue(purchase.getMSku())))
                .putCurrency(Currency.getInstance("USD"))
                .putItemName(purchase.getMSku())
                .putItemType("iap")
                .putItemId(purchase.getMSku())
                .putSuccess(true));
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://13.59.180.140:3001/api/purchase";
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestProperty("Content-type", "text/plain");
                    httpURLConnection.getOutputStream().write(dataSignature.getBytes());
                    httpURLConnection.getOutputStream().write("\n".getBytes());
                    httpURLConnection.getOutputStream().write(purchaseData.getBytes());
                    httpURLConnection.getOutputStream().flush();
                    Log.i("IapHelper", "Response code: " + httpURLConnection.getResponseCode());
                } catch (Exception e) {
                    Log.w("IapHelper", "Failed to save purchase");
                }
            }
        }).start();
        mService.consumePurchase(3, activity.getPackageName(), purchase.getMToken());
        } catch (JSONException e) {
            Log.w("IapHelper", "Cannot parse purchase " + purchaseData);
        } catch (RemoteException e) {
            Log.e("IapHelper", "Cannot consume purchase" + purchaseData);
        }
    }

    private double getPurchaseValue(final String sku) {
        if (sku == null) {
            return 0.01;
        }
        switch (sku) {
            case "donate_1":
                return 1;
            case "donate_5":
                return 5;
            case "donate_25":
                return 25;
            default:
                return 0.01;
        }
    }

    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        mService = IInAppBillingService.Stub.asInterface(service);
        String packageName = activity.getPackageName();
        try {
            int response = mService.isBillingSupported(3, packageName, ITEM_TYPE_INAPP);
            if (response != BILLING_RESPONSE_RESULT_OK) {
                Toast.makeText(activity, R.string.error_iap_support, Toast.LENGTH_LONG).show();
                return;
            }
            connected = true;
            iapCallback.onConnected();
        } catch (RemoteException e) {
            Toast.makeText(activity, R.string.error_iap_remote, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onServiceDisconnected(final ComponentName name) {
        connected = false;
        iapCallback.onDisconnected();
    }

    public interface IapCallback {
        void onConnected();

        void onDisconnected();
    }
}

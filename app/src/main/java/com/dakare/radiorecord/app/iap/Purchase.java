package com.dakare.radiorecord.app.iap;

import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;

@Getter
public class Purchase
{
    private final String mOrderId;
    private final String mPackageName;
    private final String mSku;
    private final long mPurchaseTime;
    private final int mPurchaseState;
    private final String mDeveloperPayload;
    private final String mToken;
    private final String mOriginalJson;
    private final String mSignature;
    private final boolean mIsAutoRenewing;

    public Purchase(final String jsonPurchaseInfo, final String signature) throws JSONException {
        mOriginalJson = jsonPurchaseInfo;
        JSONObject o = new JSONObject(mOriginalJson);
        mOrderId = o.optString("orderId");
        mPackageName = o.optString("packageName");
        mSku = o.optString("productId");
        mPurchaseTime = o.optLong("purchaseTime");
        mPurchaseState = o.optInt("purchaseState");
        mDeveloperPayload = o.optString("developerPayload");
        mToken = o.optString("token", o.optString("purchaseToken"));
        mIsAutoRenewing = o.optBoolean("autoRenewing");
        mSignature = signature;
    }
}

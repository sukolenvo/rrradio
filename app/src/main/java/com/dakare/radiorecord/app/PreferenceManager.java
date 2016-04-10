package com.dakare.radiorecord.app;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager
{
    private static final String NAME ="radio_record";

    private static final String QUALITY_KEY = "quality_string";

    private static PreferenceManager INSTANCE;
    private final SharedPreferences sharedPreferences;

    private PreferenceManager(Context context)
    {
        sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static PreferenceManager getInstance(Context context)
    {
        if (INSTANCE == null)
        {
            INSTANCE = new PreferenceManager(context);
        }
        return INSTANCE;
    }

    public void setDefaultQuality(String quality)
    {
        sharedPreferences.edit()
                .putString(QUALITY_KEY, quality)
                .apply();
    }

    public String getDefaultQuality()
    {
        return sharedPreferences.getString(QUALITY_KEY, "320");
    }
}

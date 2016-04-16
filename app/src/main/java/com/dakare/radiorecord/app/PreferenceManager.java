package com.dakare.radiorecord.app;

import android.content.Context;
import android.content.SharedPreferences;
import com.dakare.radiorecord.app.quality.Quality;

public class PreferenceManager
{
    private static final String NAME ="radio_record";

    private static final String QUALITY_KEY = "quality";

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

    public void setDefaultQuality(Quality quality)
    {
        sharedPreferences.edit()
                .putString(QUALITY_KEY, quality.name())
                .apply();
    }

    public Quality getDefaultQuality()
    {
        String qualityName = sharedPreferences.getString(QUALITY_KEY, null);
        return qualityName == null ? null : Quality.valueOf(qualityName);
    }
}

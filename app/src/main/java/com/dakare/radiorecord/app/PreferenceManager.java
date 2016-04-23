package com.dakare.radiorecord.app;

import android.content.Context;
import android.content.SharedPreferences;
import com.dakare.radiorecord.app.quality.Quality;

import java.util.*;

public class PreferenceManager
{
    private static final String NAME ="radio_record";

    private static final String QUALITY_KEY = "quality";
    private static final String STATIONS_KEY = "stations";

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

    public void setStations(List<Station> stations)
    {
        StringBuilder result = new StringBuilder();
        for (Station station : stations)
        {
            result.append(station.name());
            result.append(",");
        }
        sharedPreferences.edit()
                .putString(STATIONS_KEY, result.toString())
                .apply();
    }

    public List<Station> getStations()
    {
        String line = sharedPreferences.getString(STATIONS_KEY, null);
        if (line == null)
        {
            return Arrays.asList(Station.values());
        }
        List<Station> stations = new ArrayList<Station>();
        for (String name : line.substring(0, line.length() - 1).split(","))
        {
            stations.add(Station.valueOf(name));
        }
        return stations;
    }
}

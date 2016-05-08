package com.dakare.radiorecord.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import com.dakare.radiorecord.app.quality.Quality;

import java.util.*;

public class PreferenceManager
{
    private static final String NAME ="radio_record";

    private static final String QUALITY_KEY = "quality";
    private static final String STATIONS_KEY = "stations";
    private static final String MUSIC_METADATA_KEY = "music_metadata";
    private static final String MUSIC_IMAGE_KEY = "music_image";
    private static final String CALL_SETTINGS_KEY = "call_settings";
    private static final String LAST_STATION = "last_station";

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
        if (quality == null)
        {
            sharedPreferences.edit()
                    .remove(QUALITY_KEY)
                    .commit();
        } else
        {
            sharedPreferences.edit()
                    .putString(QUALITY_KEY, quality.name())
                    .apply();
        }
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

    public boolean isMusicMetadataEnabled()
    {
        return sharedPreferences.getBoolean(MUSIC_METADATA_KEY, true);
    }

    public void setMusicMedatada(final boolean enabled)
    {
        sharedPreferences.edit()
                .putBoolean(MUSIC_METADATA_KEY, enabled)
                .apply();
    }

    public boolean isMusicImageEnabled()
    {
        return sharedPreferences.getBoolean(MUSIC_IMAGE_KEY, true);
    }

    public void setMusicImage(final boolean enabled)
    {
        sharedPreferences.edit()
                .putBoolean(MUSIC_IMAGE_KEY, enabled)
                .apply();
    }

    public boolean isOnCallEnabled()
    {
        return sharedPreferences.getBoolean(CALL_SETTINGS_KEY, Build.VERSION.SDK_INT < Build.VERSION_CODES.M);
    }

    public void setOnCall(final boolean enabled)
    {
        sharedPreferences.edit()
                .putBoolean(CALL_SETTINGS_KEY, enabled)
                .apply();
    }

    public Station getLastStation()
    {
        return Station.valueOf(sharedPreferences.getString(LAST_STATION, Station.RADIO_RECORD.name()));
    }

    public void setLastStation(final Station station)
    {
        sharedPreferences.edit()
                .putString(LAST_STATION, station.name())
                .apply();
    }
}

package com.dakare.radiorecord.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import com.dakare.radiorecord.app.database.DownloadAudioTable;
import com.dakare.radiorecord.app.download.service.DownloadsSort;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.equalizer.EqualizerSettings;
import com.dakare.radiorecord.app.quality.Quality;
import com.google.gson.Gson;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PreferenceManager {
    private static final String NAME = "radio_record";

    private static final String QUALITY_KEY = "quality";
    private static final String STATIONS_KEY = "stations";
    private static final String MUSIC_METADATA_KEY = "music_metadata";
    private static final String MUSIC_IMAGE_KEY = "music_image";
    private static final String CALL_SETTINGS_KEY = "call_settings";
    private static final String LAST_STATION = "last_station";
    private static final String SORT_HISTORY_FROM_OLD = "history_sort";
    private static final String HISTORY_SHOW_ALL = "hisoty_show_all";
    private static final String LAST_PLAYLIST_KEY = "last_playlist";
    private static final String DOWNLOAD_DIRECTORY_KEY = "download_directory";
    private static final String DOWNLOADS_SORT_KEY = "downloads_sort";
    private static final String MAIN_HINT = "main_hint";
    private static final String LOAD_HINT = "load_hint";
    public static final String EQ_ENABLED_KEY = "equalizer_enabled";
    private static final String EQ_BANDS_KEY = "equalizer_bands";
    private static final String EQ_RANGE_KEY = "equalizer_range";
    public static final String EQ_LEVELS_KEY = "equalizer_level";

    private static PreferenceManager INSTANCE;
    private final SharedPreferences sharedPreferences;

    private PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static PreferenceManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new PreferenceManager(context);
        }
        return INSTANCE;
    }

    public void setDefaultQuality(Quality quality) {
        if (quality == null) {
            sharedPreferences.edit()
                    .remove(QUALITY_KEY)
                    .apply();
        } else {
            sharedPreferences.edit()
                    .putString(QUALITY_KEY, quality.name())
                    .apply();
        }
    }

    public Quality getDefaultQuality(final Quality defaultValue) {
        String qualityName = sharedPreferences.getString(QUALITY_KEY, null);
        return qualityName == null ? defaultValue : Quality.valueOf(qualityName);
    }

    public void setStations(List<Station> stations) {
        StringBuilder result = new StringBuilder();
        for (Station station : stations) {
            result.append(station.name());
            result.append(",");
        }
        sharedPreferences.edit()
                .putString(STATIONS_KEY, result.toString())
                .apply();
    }

    public List<Station> getStations() {
        String line = sharedPreferences.getString(STATIONS_KEY, null);
        if (line == null) {
            return Arrays.asList(Station.values());
        }
        List<Station> stations = new ArrayList<Station>();
        for (String name : line.substring(0, line.length() - 1).split(",")) {
            stations.add(Station.valueOf(name));
        }
        return stations;
    }

    public boolean isMusicMetadataEnabled() {
        return sharedPreferences.getBoolean(MUSIC_METADATA_KEY, true);
    }

    public void setMusicMedatada(final boolean enabled) {
        sharedPreferences.edit()
                .putBoolean(MUSIC_METADATA_KEY, enabled)
                .apply();
    }

    public boolean isMusicImageEnabled() {
        return sharedPreferences.getBoolean(MUSIC_IMAGE_KEY, true);
    }

    public void setMusicImage(final boolean enabled) {
        sharedPreferences.edit()
                .putBoolean(MUSIC_IMAGE_KEY, enabled)
                .apply();
    }

    public boolean isOnCallEnabled() {
        return sharedPreferences.getBoolean(CALL_SETTINGS_KEY, Build.VERSION.SDK_INT < Build.VERSION_CODES.M);
    }

    public void setOnCall(final boolean enabled) {
        sharedPreferences.edit()
                .putBoolean(CALL_SETTINGS_KEY, enabled)
                .apply();
    }

    public Station getLastStation() {
        return Station.valueOf(sharedPreferences.getString(LAST_STATION, Station.RADIO_RECORD.name()));
    }

    public void setLastStation(final Station station) {
        sharedPreferences.edit()
                .putString(LAST_STATION, station.name())
                .apply();
    }

    public boolean isHistoryShowAll() {
        return sharedPreferences.getBoolean(HISTORY_SHOW_ALL, false);
    }

    public void setHistoryShowAll(final boolean allItems) {
        sharedPreferences.edit()
                .putBoolean(HISTORY_SHOW_ALL, allItems)
                .apply();
    }

    public boolean isHistorySortOld() {
        return sharedPreferences.getBoolean(SORT_HISTORY_FROM_OLD, true);
    }

    public void setHistorySortOld(final boolean fromOld) {
        sharedPreferences.edit()
                .putBoolean(SORT_HISTORY_FROM_OLD, fromOld)
                .apply();
    }

    public List<PlaylistItem> getLastPlaylist() {
        String list = sharedPreferences.getString(LAST_PLAYLIST_KEY, null);
        if (TextUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        Gson gson = new Gson();
        try {
            return gson.fromJson(list, new ParameterizedType() {
                @Override
                public Type[] getActualTypeArguments() {
                    return new Type[]{PlaylistItem.class};
                }

                @Override
                public Type getOwnerType() {
                    return null;
                }

                @Override
                public Type getRawType() {
                    return List.class;
                }
            });
        } catch (Exception e) {
            Log.e("PrefManager", "Failed to init playlist", e);
            return Collections.emptyList();
        }
    }

    public void setLastPlaylist(final List<PlaylistItem> playlist) {
        sharedPreferences.edit()
                .putString(LAST_PLAYLIST_KEY, new Gson().toJson(playlist))
                .apply();
    }

    public void setDownloadDirectory(final String directory) {
        sharedPreferences.edit()
                .putString(DOWNLOAD_DIRECTORY_KEY, directory)
                .apply();
    }

    public String getDownloadDirectory() {
        String path = sharedPreferences.getString(DOWNLOAD_DIRECTORY_KEY, null);
        if (path == null) {
            path = new File(Environment.getExternalStorageDirectory(), DownloadAudioTable.DEFAULT_DIRECTORY_NAME).getAbsolutePath();
            setDownloadDirectory(path);
        }
        return path;
    }

    public DownloadsSort getDownloadsSort() {
        return DownloadsSort.valueOf(sharedPreferences.getString(DOWNLOADS_SORT_KEY, DownloadsSort.NAME_ASC.name()));
    }

    public void setDownloadsSort(final DownloadsSort downloadsSort) {
        sharedPreferences.edit().putString(DOWNLOADS_SORT_KEY, downloadsSort.name())
                .apply();
    }

    public boolean showMainHint() {
        return sharedPreferences.getBoolean(MAIN_HINT, true);
    }

    public void hideMainHint() {
        sharedPreferences.edit()
                .putBoolean(MAIN_HINT, false)
                .apply();
    }


    public boolean showLoadHint() {
        return sharedPreferences.getBoolean(LOAD_HINT, true);
    }

    public void hideLoadHint() {
        sharedPreferences.edit()
                .putBoolean(LOAD_HINT, false)
                .apply();
    }

    public EqualizerSettings getEqSettings() {
        EqualizerSettings equalizerSettings = new EqualizerSettings();
        equalizerSettings.setEnabled(sharedPreferences.getBoolean(EQ_ENABLED_KEY, false));
        if (equalizerSettings.isEnabled()) {
            equalizerSettings.setBands(toIntArray(sharedPreferences.getString(EQ_BANDS_KEY, "")));
            equalizerSettings.setRange(toIntArray(sharedPreferences.getString(EQ_RANGE_KEY, "")));
            equalizerSettings.setLevels(toIntArray(sharedPreferences.getString(EQ_LEVELS_KEY, "")));
        }
        return equalizerSettings;
    }

    private int[] toIntArray(final String string) {
        String[] values = string.split(",");
        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = Integer.parseInt(values[i]);
        }
        return result;
    }

    public void setEqSettings(final EqualizerSettings eqSettings) {
        SharedPreferences.Editor editor = sharedPreferences.edit()
                .putBoolean(EQ_ENABLED_KEY, eqSettings.isEnabled());
        if (eqSettings.isEnabled()) {
            editor.putString(EQ_BANDS_KEY, joinIntArray(eqSettings.getBands()));
            editor.putString(EQ_RANGE_KEY, joinIntArray(eqSettings.getRange()));
            editor.putString(EQ_LEVELS_KEY, joinIntArray(eqSettings.getLevels()));
        }
        editor.apply();
    }

    private String joinIntArray(final int[] array) {
        if (array == null || array.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            builder.append(',');
            builder.append(array[i]);
        }
        return builder.toString();
    }

    public void registerChangeListener(final SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterChangeListener(final SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }
}

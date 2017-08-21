package com.dakare.radiorecord.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import com.dakare.radiorecord.app.database.table.DownloadAudioTable;
import com.dakare.radiorecord.app.download.service.DownloadsSort;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.equalizer.EqualizerSettings;
import com.dakare.radiorecord.app.player.sleep_mode.SleepMode;
import com.dakare.radiorecord.app.player.sleep_mode.SleepSettings;
import com.dakare.radiorecord.app.quality.Quality;
import com.dakare.radiorecord.app.utils.JsonHelper;
import com.dakare.radiorecord.app.view.theme.Theme;

import java.io.File;
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
    @Deprecated
    private static final String CALL_SETTINGS_KEY = "call_settings";
    private static final String LAST_STATION = "last_station";
    private static final String SORT_HISTORY_FROM_OLD = "history_sort";
    private static final String HISTORY_SHOW_ALL = "hisoty_show_all";
    public static final String LAST_PLAYLIST_KEY = "last_playlist";
    private static final String DOWNLOAD_DIRECTORY_KEY = "download_directory";
    private static final String DOWNLOADS_SORT_KEY = "downloads_sort";
    private static final String MAIN_HINT = "main_hint";
    private static final String LOAD_HINT = "load_hint";
    public static final String EQ_ENABLED_KEY = "equalizer_enabled";
    private static final String EQ_BANDS_KEY = "equalizer_bands";
    private static final String EQ_RANGE_KEY = "equalizer_range";
    public static final String EQ_LEVELS_KEY = "equalizer_level";
    public static final String EQ_PRESET_KEY = "equalizer_preset";
    private static final String EQ_SETTINGS_KEY = "equalizer_settings";
    public static final String LAST_PLAYLIST_POSITION_KEY = "last_playlist_position";
    private static final String SLEEP_SETTINGS_KEY = "sleep_settings_";
    public static final String SLEEP_MODE_KEY = "sleep_mode";
    private static final String SLEEP_MODE_STARTED = "sleep_mode_started";
    private static final String THEME_MODE_KEY = "theme";
    private static final String THEME_PROMPT_KEY = "theme_promt";
    private static final String SHOW_AD_KEY = "show_ads";
    private static final String PHONE_NUMBER_KEY = "phone_number";

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
            try {
                stations.add(Station.valueOf(name));
            } catch (IllegalArgumentException e) {
                //station not exists anymore
            }
        }
        if (stations.size() < Station.values().length) {
            for (Station station : Station.values()) {
                if (!stations.contains(station)) {
                    stations.add(station);
                }
            }
            setStations(stations);
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

    public Station getLastStation() {
        return Station.valueOf(sharedPreferences.getString(LAST_STATION, Station.RADIO_RECORD.name()));
    }

    public void setLastStation(final Station station) {
        sharedPreferences.edit()
                .putString(LAST_STATION, station.name())
                .apply();
    }

    public int getLastPosition() {
        return sharedPreferences.getInt(LAST_PLAYLIST_POSITION_KEY, 0);
    }

    public void setLastPosition(final int position) {
        sharedPreferences.edit()
                .putInt(LAST_PLAYLIST_POSITION_KEY, position)
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
        try {
            return JsonHelper.readPlaylist(list);
        } catch (Exception e) {
            Log.e("PrefManager", "Failed to init playlist", e);
            return Collections.singletonList(new PlaylistItem(Station.RADIO_RECORD, Quality.HIGH));
        }
    }

    public void setLastPlaylist(final List<PlaylistItem> playlist) {
        sharedPreferences.edit()
                .putString(LAST_PLAYLIST_KEY, JsonHelper.writePlaylist(playlist))
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
            equalizerSettings.setPreset(sharedPreferences.getString(EQ_PRESET_KEY, null));
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
            if (eqSettings.getPreset() != null) {
                editor.putString(EQ_PRESET_KEY, eqSettings.getPreset());
            }
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

    public boolean isEqSettingsEnabled() {
        return sharedPreferences.getBoolean(EQ_SETTINGS_KEY, Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN);
    }

    public void setEqSettings(final boolean enabled) {
        sharedPreferences.edit()
                .putBoolean(EQ_SETTINGS_KEY, enabled)
                .apply();
    }

    public SleepSettings getSleepSettings(final SleepMode sleepMode) {
        String value = sharedPreferences.getString(SLEEP_SETTINGS_KEY + sleepMode.name(), null);
        if (TextUtils.isEmpty(value)) {
            switch (sleepMode) {
                case OFF:
                    return new SleepSettings();
                case ELAPSED_S:
                    return new SleepSettings(0, 30);
                case ELAPSED_M:
                    return new SleepSettings(1, 0);
                case ELAPSED_L:
                    return new SleepSettings(2, 30);
                case CHOSEN_1:
                    return new SleepSettings(22, 0);
                case CHOSEN_2:
                    return new SleepSettings(23, 30);
            }
        }
        return SleepSettings.unmarshal(value);
    }

    public void setSleepSettings(final SleepMode sleepMode, final SleepSettings sleepSettings) {
        sharedPreferences.edit()
                .putString(SLEEP_SETTINGS_KEY + sleepMode.name(), sleepSettings.marchal())
                .apply();
    }

    public SleepMode getSleepMode() {
        return SleepMode.valueOf(sharedPreferences.getString(SLEEP_MODE_KEY, SleepMode.OFF.name()));
    }

    public void setSleepMode(final SleepMode sleepMode) {
        sharedPreferences.edit()
                .putString(SLEEP_MODE_KEY, sleepMode.name())
                .putLong(SLEEP_MODE_STARTED, System.currentTimeMillis())
                .apply();
    }

    public long getSleepModeTs() {
        return sharedPreferences.getLong(SLEEP_MODE_STARTED, System.currentTimeMillis());
    }

    public Theme getTheme() {
        return Theme.valueOf(sharedPreferences.getString(THEME_MODE_KEY, Theme.CLASSIC.name()));
    }

    public void setTheme(final Theme theme) {
        sharedPreferences.edit()
                .putString(THEME_MODE_KEY, theme.name())
                .apply();
    }

    public boolean getThemePrompt() {
        return sharedPreferences.getBoolean(THEME_PROMPT_KEY, true);
    }

    public void setThemePrompt(final boolean value) {
        sharedPreferences.edit()
                .putBoolean(THEME_PROMPT_KEY, value)
                .apply();
    }

    public boolean getShowAd() {
        return sharedPreferences.getBoolean(SHOW_AD_KEY, !BuildConfig.DEBUG);
    }

    public void setShowAd(final boolean value) {
        sharedPreferences.edit()
                .putBoolean(SHOW_AD_KEY, value)
                .apply();
    }

    public String getPhoneNumber() {
        return sharedPreferences.getString(PHONE_NUMBER_KEY, null);
    }

    public void setPhoneNumber(final String value) {
        sharedPreferences.edit()
                .putString(PHONE_NUMBER_KEY, value)
                .apply();
    }
}

package com.dakare.radiorecord.app.load.history;

import android.os.Bundle;
import com.dakare.radiorecord.app.load.loader.BasicCategoryLoader;
import com.dakare.radiorecord.app.load.loader.CategoryLoader;
import com.dakare.radiorecord.app.load.loader.database.HistoryMusicCategoryDbTable;
import com.dakare.radiorecord.app.load.loader.parser.HistoryMusicParser;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionAdapter;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionFragment;
import com.dakare.radiorecord.app.station.DynamicStation;

public class HistoryMusicSelectFragment extends AbstractSelectionFragment<HistoryMusicSelectAdapter.ViewHolder, HistoryMusicItem> {

    public static final String STATION_KEY = "station_key";
    public static final String DATE_KEY = "date_key";
    private static final String URL_TEMPLATE = "http://history.radiorecord.ru/index-onstation.php?station=%s&day=%s";

    private HistoryMusicSelectAdapter adapter;
    private CategoryLoader<HistoryMusicItem> categoryLoader;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        DynamicStation station = DynamicStation.deserialize(getArguments().getString(STATION_KEY));
        String date = getArguments().getString(DATE_KEY);
        super.onCreate(savedInstanceState);
        adapter = new HistoryMusicSelectAdapter(getContext(), station, getSelectionManager(), this);
        categoryLoader = new BasicCategoryLoader<>(new HistoryMusicCategoryDbTable(station, date), new HistoryMusicParser(),
                String.format(URL_TEMPLATE, station.getKey(), date));
    }

    @Override
    protected AbstractSelectionAdapter<HistoryMusicSelectAdapter.ViewHolder, HistoryMusicItem> getAdapter() {
        return adapter;
    }

    @Override
    protected CategoryLoader<HistoryMusicItem> getCategoryLoader() {
        return categoryLoader;
    }

    public void onPreferenceChanged() {
        adapter.onPrefChanged();
    }
}

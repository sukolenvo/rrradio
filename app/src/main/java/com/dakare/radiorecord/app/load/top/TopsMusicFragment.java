package com.dakare.radiorecord.app.load.top;

import android.os.Bundle;
import com.dakare.radiorecord.app.station.AbstractStation;
import com.dakare.radiorecord.app.load.loader.BasicCategoryLoader;
import com.dakare.radiorecord.app.load.loader.CategoryLoader;
import com.dakare.radiorecord.app.load.loader.database.TopsCategoryDbTable;
import com.dakare.radiorecord.app.load.loader.parser.TopsParser;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionAdapter;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionFragment;

public class TopsMusicFragment extends AbstractSelectionFragment<TopsMusicSelectAdapter.ViewHolder, TopsMusicItem> {
    public static final String STATION_KEY = "station_key";
    private static final String URL_TEMPLATE = "http://www.radiorecord.ru/radio/top100/%s.txt";

    private AbstractStation station;
    private TopsMusicSelectAdapter adapter;
    private CategoryLoader<TopsMusicItem> categoryLoader;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        station = AbstractStation.deserialize(getArguments().getString(STATION_KEY));
        super.onCreate(savedInstanceState);
        adapter = new TopsMusicSelectAdapter(getContext(), station, getSelectionManager(), this);
        categoryLoader = new BasicCategoryLoader<>(new TopsCategoryDbTable(station), new TopsParser(),
                String.format(URL_TEMPLATE, station.getCodeAsParam()));
    }

    @Override
    protected AbstractSelectionAdapter<TopsMusicSelectAdapter.ViewHolder, TopsMusicItem> getAdapter() {
        return adapter;
    }

    @Override
    protected CategoryLoader<TopsMusicItem> getCategoryLoader() {
        return categoryLoader;
    }
}

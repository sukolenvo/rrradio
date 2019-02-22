package com.dakare.radiorecord.app.load.history;

import android.os.Bundle;
import com.dakare.radiorecord.app.station.AbstractStation;
import com.dakare.radiorecord.app.load.AbstractLoadAdapter;
import com.dakare.radiorecord.app.load.loader.BasicCategoryLoader;
import com.dakare.radiorecord.app.load.loader.CategoryLoader;
import com.dakare.radiorecord.app.load.loader.database.HistoryDateCategoryDbTable;
import com.dakare.radiorecord.app.load.loader.parser.HistoryDateParser;

public class HistoryDateSelectFragment extends AbstractHistoryMediatorFragment<HistoryDateSelectAdapter.ViewHolder, String> {
    public static final String STATION_KEY = "station_key";
    private static final String URL_TEMPLATE = "http://history.radiorecord.ru/index-onstation.php?station=";

    private HistoryDateSelectAdapter adapter;
    private CategoryLoader<String> categoryLoader;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        AbstractStation station = AbstractStation.deserialize(getArguments().getString(STATION_KEY));
        super.onCreate(savedInstanceState);
        adapter = new HistoryDateSelectAdapter(getContext(), getMediator(), station);
        categoryLoader = new BasicCategoryLoader<>(new HistoryDateCategoryDbTable(station), new HistoryDateParser(),
                URL_TEMPLATE + station.getCodeAsParam());
    }

    @Override
    protected AbstractLoadAdapter<HistoryDateSelectAdapter.ViewHolder, String> getAdapter() {
        return adapter;
    }

    @Override
    protected CategoryLoader<String> getCategoryLoader() {
        return categoryLoader;
    }
}

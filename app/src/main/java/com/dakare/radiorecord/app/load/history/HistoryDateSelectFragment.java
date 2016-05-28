package com.dakare.radiorecord.app.load.history;

import android.os.Bundle;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.load.AbstractLoadAdapter;
import com.dakare.radiorecord.app.load.AbstractLoadFragment;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryDateSelectFragment extends AbstractHistoryMediatorFragment<HistoryDateSelectAdapter.ViewHolder, String>
{
    private static final String ITEMS_KEY = "history_items";
    public static final String STATION_KEY = "station_key";
    private static final String URL_TEMPLATE = "http://history.radiorecord.ru/index-onstation.php?station=";

    private Station station;
    private HistoryDateSelectAdapter adapter;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        station = Station.valueOf(getArguments().getString(STATION_KEY));
        super.onCreate(savedInstanceState);
        adapter = new HistoryDateSelectAdapter(getContext(), getMediator(), station);
    }

    @Override
    protected List<String> restoreItems(final Bundle args)
    {
        return args.containsKey(ITEMS_KEY) ? args.getStringArrayList(ITEMS_KEY) : Collections.EMPTY_LIST;
    }

    @Override
    protected AbstractLoadAdapter<HistoryDateSelectAdapter.ViewHolder, String> getAdapter()
    {
        return adapter;
    }

    @Override
    protected void saveItems(final ArrayList<String> items, final Bundle outState)
    {
        outState.putStringArrayList(ITEMS_KEY, items);
    }

    @Override
    protected List<String> startLoading() throws IOException
    {
        setStatus(R.string.message_loading);
        List<String> result;
        Connection.Response response = Jsoup.connect(URL_TEMPLATE + station.getCodeAsParam())
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36")
                .execute();
        if (isDestroyed())
        {
            return Collections.emptyList();
        }
        setStatus(R.string.message_preparing);
        Document doc = response.parse();
        if (isDestroyed())
        {
            return Collections.emptyList();
        }
        setStatus(R.string.message_search_entries);
        Elements elements = doc.select("div.daytabs div.aday");
        int index = 1;
        result = new ArrayList<String>(elements.size());
        for (Element element : elements)
        {
            if (isDestroyed())
            {
                return Collections.emptyList();
            }
            setStatus(R.string.message_parsing_progres, index++, elements.size());
            if (element.hasAttr("value"))
            {
                result.add(element.attr("value"));
            }
        }
        return result;
    }
}

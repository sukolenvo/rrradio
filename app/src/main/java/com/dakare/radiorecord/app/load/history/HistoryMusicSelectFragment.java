package com.dakare.radiorecord.app.load.history;

import android.os.Bundle;
import android.util.Log;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
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

public class HistoryMusicSelectFragment extends AbstractHistoryMediatorFragment<HistoryMusicSelectAdapter.ViewHolder, HistoryMusicItem>
{
    private static final String ITEMS_KEY = "history_items";
    public static final String STATION_KEY = "station_key";
    public static final String DATE_KEY = "date_key";
    private static final String URL_TEMPLATE = "http://history.radiorecord.ru/index-onstation.php?station=%s&day=%s";

    private Station station;
    private String date;
    private HistoryMusicSelectAdapter adapter;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        station = Station.valueOf(getArguments().getString(STATION_KEY));
        date = getArguments().getString(DATE_KEY);
        super.onCreate(savedInstanceState);
        adapter = new HistoryMusicSelectAdapter(getContext(), getMediator(), station);
    }

    @Override
    protected List<HistoryMusicItem> restoreItems(final Bundle args)
    {
        return args.containsKey(ITEMS_KEY) ? args.getParcelableArrayList(ITEMS_KEY) : Collections.EMPTY_LIST;
    }

    @Override
    protected AbstractLoadAdapter<HistoryMusicSelectAdapter.ViewHolder, HistoryMusicItem> getAdapter()
    {
        return adapter;
    }

    @Override
    protected void saveItems(final ArrayList<HistoryMusicItem> items, final Bundle outState)
    {
        outState.putParcelableArrayList(ITEMS_KEY, items);
    }

    @Override
    protected List<HistoryMusicItem> startLoading() throws IOException
    {
        setStatus(R.string.message_loading);
        List<HistoryMusicItem> result;
        Connection.Response response = Jsoup.connect(String.format(URL_TEMPLATE, station.getCodeAsParam(), date))
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
        Elements elements = doc.select("article.track-holder");
        int index = 1;
        result = new ArrayList<HistoryMusicItem>(elements.size());
        if (isDestroyed())
        {
            return Collections.emptyList();
        }
        for (Element element : elements)
        {
            setStatus(R.string.message_parsing_progres, index++, elements.size());
            if (isDestroyed())
            {
                return Collections.emptyList();
            }
            HistoryMusicItem item = new HistoryMusicItem();
            item.setVisible(!element.attr("class").contains("hidden"));
            Elements whenElements = element.select("div.place-num");
            if (whenElements.size() == 1)
            {
                item.setWhen(whenElements.get(0).ownText());
            }  else
            {
                Log.e("ParserFactory", "Cannot parse element[when]");
                continue;
            }
            Elements urlElements = element.select("td.play_pause");
            if (urlElements.size() == 1 && urlElements.get(0).hasAttr("item_url"))
            {
                item.setUrl(urlElements.get(0).attr("item_url"));
            }  else
            {
                Log.e("ParserFactory", "Cannot parse element[url]");
                continue;
            }
            Elements artistElements = element.select("span.artist");
            if (artistElements.size() == 1)
            {
                item.setArtist(artistElements.get(0).ownText());
            }  else
            {
                Log.e("ParserFactory", "Cannot parse element[artist]");
                continue;
            }
            Elements nameElements = element.select("span.name");
            if (nameElements.size() == 1)
            {
                item.setSong(nameElements.get(0).ownText());
            }  else
            {
                Log.e("ParserFactory", "Cannot parse element[song]");
                continue;
            }
            result.add(item);
        }
        if (result.size() < elements.size())
        {
            sendToast(RecordApplication.getInstance().getString(R.string.message_parsing_summary, result.size(), elements.size() - result.size()));
        }
        return result;
    }

    public void onPreferenceChanged()
    {
        adapter.onPrefChanged();
    }
}

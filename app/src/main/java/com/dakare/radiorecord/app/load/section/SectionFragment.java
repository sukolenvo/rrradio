package com.dakare.radiorecord.app.load.section;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.load.AbstractLoadAdapter;
import com.dakare.radiorecord.app.load.AbstractLoadFragment;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionAdapter;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionFragment;
import com.dakare.radiorecord.app.load.selection.SelectionCallback;
import com.dakare.radiorecord.app.load.selection.SelectionManager;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SectionFragment extends AbstractSelectionFragment<SectionAdapter.ViewHolder, SectionMusicItem>
{
    private static final String ITEMS_KEY = "section_items";
    public static final String CATEGORY_KEY = "category";
    private static final String CATEGORY_LIST_URL = "http://www.radiorecord.ru/pda_new/superchart";

    private String categoryName;
    private SectionAdapter adapter;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        categoryName = getArguments().getString(CATEGORY_KEY);
        super.onCreate(savedInstanceState);
        adapter = new SectionAdapter(getContext(), getSelectionManager(), this);
    }

    @Override
    protected List<SectionMusicItem> restoreItems(final Bundle args)
    {
        return args.containsKey(ITEMS_KEY) ? args.getParcelableArrayList(ITEMS_KEY) : Collections.EMPTY_LIST;
    }

    @Override
    protected AbstractSelectionAdapter<SectionAdapter.ViewHolder, SectionMusicItem> getAdapter()
    {
        return adapter;
    }

    @Override
    protected void saveItems(final ArrayList<SectionMusicItem> items, final Bundle outState)
    {
        outState.putParcelableArrayList(ITEMS_KEY, items);
    }

    @Override
    protected List<SectionMusicItem> startLoading() throws IOException
    {
        setStatus(R.string.message_loading_category);
        List<SectionMusicItem> result;
        Connection.Response response = Jsoup.connect(CATEGORY_LIST_URL)
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
        String url = null;
        for (Element element : doc.select("a.playlist-block"))
        {
            for (Element subItem : element.select("span.playlist-info"))
            {
                if (categoryName.equalsIgnoreCase(subItem.ownText()))
                {
                    url = element.attr("abs:href");
                    break;
                }
            }
            if (url != null)
            {
                break;
            }
        }
        if (url == null)
        {
            sendToast(RecordApplication.getInstance().getString(R.string.message_failed_category));
            return Collections.emptyList();
        }
        if (isDestroyed())
        {
            return Collections.emptyList();
        }
        setStatus(R.string.message_loading_music);
        response = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36")
                .execute();
        if (isDestroyed())
        {
            return Collections.emptyList();
        }
        setStatus(R.string.message_preparing);
        doc = response.parse();
        if (isDestroyed())
        {
            return Collections.emptyList();
        }
        setStatus(R.string.message_search_entries);
        Elements elements = doc.select("div.chart-item");
        int index = 1;
        result = new ArrayList<>(elements.size());
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
            SectionMusicItem item = new SectionMusicItem();
            Elements urlElements = element.select("a.play-icon");
            if (urlElements.size() == 1 && urlElements.get(0).hasAttr("href"))
            {
                item.setUrl(urlElements.get(0).attr("href"));
            }  else
            {
                Log.e("SectionFragment", "Cannot parse element[url]");
                continue;
            }
            Elements artistElements = element.select("#chart-item-arist-name");
            if (artistElements.size() == 1)
            {
                item.setArtist(artistElements.get(0).ownText());
            }  else
            {
                Log.e("SectionFragment", "Cannot parse element[artist]");
                continue;
            }
            Elements nameElements = element.select("#chart-item-name");
            if (nameElements.size() == 1)
            {
                String name = nameElements.get(0).ownText();
                if (!TextUtils.isEmpty(name))
                {
                    item.setSong(name);
                }
            }  else
            {
                Log.e("SectionFragment", "Cannot parse element[song]");
                continue;
            }
            result.add(item);
        }
        return result;
    }

}

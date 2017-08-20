package com.dakare.radiorecord.app.load.loader.parser;

import android.util.Log;
import com.dakare.radiorecord.app.load.top.TopsMusicItem;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class TopsParser implements CategoryParser<TopsMusicItem> {

    @Override
    public List<TopsMusicItem> parse(Document document) {
        Elements elements = document.select("article.track-holder");
        List<TopsMusicItem> result = new ArrayList<>(elements.size());
        for (Element element : elements) {
            TopsMusicItem item = new TopsMusicItem();
            Elements urlElements = element.select("td.play_pause");
            if (urlElements.size() == 1 && urlElements.get(0).hasAttr("item_url")) {
                item.setUrl(urlElements.get(0).attr("item_url"));
            } else {
                Log.e("Tops Fragment", "Cannot parse element[url]");
                continue;
            }
            Elements artistElements = element.select("span.artist");
            if (artistElements.size() == 1) {
                item.setArtist(artistElements.get(0).ownText());
            } else {
                Log.e("Tops Fragment", "Cannot parse element[artist]");
                continue;
            }
            Elements nameElements = element.select("span.name");
            if (nameElements.size() == 1) {
                item.setSong(nameElements.get(0).ownText());
            } else {
                Log.e("ParserFactory", "Cannot parse element[song]");
                continue;
            }
            result.add(item);
        }
        return result;
    }
}

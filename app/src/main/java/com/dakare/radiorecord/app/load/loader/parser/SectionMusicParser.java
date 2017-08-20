package com.dakare.radiorecord.app.load.loader.parser;

import android.text.TextUtils;
import android.util.Log;
import com.dakare.radiorecord.app.load.section.SectionMusicItem;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class SectionMusicParser implements CategoryParser<SectionMusicItem> {

    @Override
    public List<SectionMusicItem> parse(Document document) {
        Elements elements = document.select("div.chart-item");
        List<SectionMusicItem> result = new ArrayList<>(elements.size());
        for (Element element : elements) {
            SectionMusicItem item = new SectionMusicItem();
            Elements urlElements = element.select("a.play-icon");
            if (urlElements.size() == 1 && urlElements.get(0).hasAttr("href")) {
                item.setUrl(urlElements.get(0).attr("href"));
            } else {
                Log.e("SectionFragment", "Cannot parse element[url]");
                continue;
            }
            Elements artistElements = element.select("#chart-item-arist-name");
            if (artistElements.size() == 1) {
                item.setArtist(artistElements.get(0).ownText());
            } else {
                Log.e("SectionFragment", "Cannot parse element[artist]");
                continue;
            }
            Elements nameElements = element.select("#chart-item-name");
            if (nameElements.size() == 1) {
                String name = nameElements.get(0).ownText();
                if (!TextUtils.isEmpty(name)) {
                    item.setSong(name);
                }
            } else {
                Log.e("SectionFragment", "Cannot parse element[song]");
                continue;
            }
            result.add(item);
        }
        return result;
    }
}

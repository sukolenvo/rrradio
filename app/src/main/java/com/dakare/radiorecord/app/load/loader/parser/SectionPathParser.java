package com.dakare.radiorecord.app.load.loader.parser;

import com.dakare.radiorecord.app.load.section.SectionPath;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class SectionPathParser implements CategoryParser<SectionPath> {

    @Override
    public List<SectionPath> parse(Document document) {
        List<SectionPath> sectionPaths = new ArrayList<>();
        for (Element element : document.select("a.playlist-block")) {
            for (Element subItem : element.select("span.playlist-info")) {
                SectionPath item = new SectionPath();
                item.setName(subItem.ownText());
                item.setUrl(element.attr("abs:href"));
                sectionPaths.add(item);
            }
        }
        return sectionPaths;
    }
}

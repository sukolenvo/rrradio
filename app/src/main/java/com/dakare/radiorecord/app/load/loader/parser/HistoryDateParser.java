package com.dakare.radiorecord.app.load.loader.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class HistoryDateParser implements CategoryParser<String> {

    @Override
    public List<String> parse(Document document) {
        List<String> result = new ArrayList<>();
        for (Element element : document.select("div.daytabs div.aday")) {
            if (element.hasAttr("value")) {
                result.add(element.attr("value"));
            }
        }
        return result;
    }
}

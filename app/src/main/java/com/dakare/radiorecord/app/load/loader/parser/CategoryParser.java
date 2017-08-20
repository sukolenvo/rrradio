package com.dakare.radiorecord.app.load.loader.parser;

import org.jsoup.nodes.Document;

import java.util.List;

public interface CategoryParser<T> {

    List<T> parse(Document document);
}

package com.dakare.radiorecord.app.load.loader;

import android.os.Handler;
import android.util.Log;
import com.dakare.radiorecord.app.load.loader.database.CategoryDbTable;
import com.dakare.radiorecord.app.load.loader.parser.CategoryParser;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BasicCategoryLoader<T> implements CategoryLoader<T>, Runnable {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36";

    private final Handler uiHandler = new Handler();
    @Setter
    private String url;
    private final CategoryDbTable<T> categoryDbTable;
    private final CategoryParser<T> parser;
    private CategoryLoadListener<T> listener;
    private boolean canceled;

    public BasicCategoryLoader(CategoryDbTable<T> categoryDbTable, CategoryParser<T> parser) {
        this.categoryDbTable = categoryDbTable;
        this.parser = parser;
    }

    public BasicCategoryLoader(CategoryDbTable<T> categoryDbTable, CategoryParser<T> parser, String url) {
        this.url = url;
        this.categoryDbTable = categoryDbTable;
        this.parser = parser;
    }

    @Override
    public void load(CategoryLoadListener<T> listener) {
        List<T> result = categoryDbTable.load();
        if (result != null && !result.isEmpty()) {
            listener.onCategoryLoaded(result);
        } else {
            this.listener = listener;
            new Thread(this).start();
        }
    }

    @Override
    public void clearCache() {
        categoryDbTable.clear();
    }

    @Override
    public void cancel() {
        canceled = true;
    }

    @Override
    public void run() {
        final List<T> result = new ArrayList<>();
        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .execute();
            result.addAll(parser.parse(response.parse()));
            if (canceled) {
                result.clear();
            } else {
                categoryDbTable.save(result);
            }
        } catch (IOException e) {
            Log.e("BasicCategoryLoader", "Failed to load category info for url: " + url, e);
        } finally {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onCategoryLoaded(result);
                }
            });
        }
    }
}

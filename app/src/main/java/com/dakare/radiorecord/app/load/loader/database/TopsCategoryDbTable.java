package com.dakare.radiorecord.app.load.loader.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.database.table.TopsCacheTable;
import com.dakare.radiorecord.app.load.top.TopsMusicItem;

import java.util.ArrayList;
import java.util.List;

public class TopsCategoryDbTable extends BasicCategoryDbTable<TopsMusicItem> {

    private final Uri uri;

    public TopsCategoryDbTable(Station station) {
        uri = Uri.withAppendedPath(Uri.parse("content://com.dakare.radiorecord.app/tops_cache"), station.getCodeAsParam());
    }

    @Override
    protected List<TopsMusicItem> fromContentValues(Cursor query) {
        List<TopsMusicItem> result = new ArrayList<>();
        while (query.moveToNext()) {
            TopsMusicItem item = new TopsMusicItem();
            item.setArtist(query.getString(query.getColumnIndex(TopsCacheTable.COLUMN_ARTIST)));
            item.setSong(query.getString(query.getColumnIndex(TopsCacheTable.COLUMN_SONG)));
            item.setUrl(query.getString(query.getColumnIndex(TopsCacheTable.COLUMN_URL)));
            result.add(item);
        }
        return result;
    }

    @Override
    protected Uri getUrl() {
        return uri;
    }

    @Override
    protected ContentValues[] toContentValues(List<TopsMusicItem> result) {
        ContentValues[] values = new ContentValues[result.size()];
        for (int i = 0; i < result.size(); i++) {
            ContentValues contentValues = new ContentValues();
            TopsMusicItem item = result.get(i);
            contentValues.put(TopsCacheTable.COLUMN_ARTIST, item.getArtist());
            contentValues.put(TopsCacheTable.COLUMN_SONG, item.getSong());
            contentValues.put(TopsCacheTable.COLUMN_URL, item.getUrl());
            values[i] = contentValues;
        }
        return values;
    }

}

package com.dakare.radiorecord.app.load.loader.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.dakare.radiorecord.app.database.table.SectionMusicCacheTable;
import com.dakare.radiorecord.app.load.section.SectionMusicItem;

import java.util.ArrayList;
import java.util.List;

public class SectionMusicCategoryDbTable extends BasicCategoryDbTable<SectionMusicItem> {

    private final Uri uri;

    public SectionMusicCategoryDbTable(String categoryName) {
        uri = Uri.withAppendedPath(Uri.parse("content://com.dakare.radiorecord.app/section_music"), categoryName);
    }

    @Override
    protected List<SectionMusicItem> fromContentValues(Cursor query) {
        List<SectionMusicItem> result = new ArrayList<>();
        while (query.moveToNext()) {
            SectionMusicItem item = new SectionMusicItem();
            item.setArtist(query.getString(query.getColumnIndex(SectionMusicCacheTable.COLUMN_ARTIST)));
            item.setSong(query.getString(query.getColumnIndex(SectionMusicCacheTable.COLUMN_SONG)));
            item.setUrl(query.getString(query.getColumnIndex(SectionMusicCacheTable.COLUMN_URL)));
            result.add(item);
        }
        return result;
    }

    @Override
    protected Uri getUrl() {
        return uri;
    }

    @Override
    protected ContentValues[] toContentValues(List<SectionMusicItem> result) {
        ContentValues[] values = new ContentValues[result.size()];
        for (int i = 0; i < result.size(); i++) {
            ContentValues contentValues = new ContentValues();
            SectionMusicItem item = result.get(i);
            contentValues.put(SectionMusicCacheTable.COLUMN_ARTIST, item.getArtist());
            contentValues.put(SectionMusicCacheTable.COLUMN_SONG, item.getSong());
            contentValues.put(SectionMusicCacheTable.COLUMN_URL, item.getUrl());
            values[i] = contentValues;
        }
        return values;
    }

}

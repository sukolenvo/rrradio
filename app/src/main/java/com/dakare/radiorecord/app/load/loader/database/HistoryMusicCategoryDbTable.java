package com.dakare.radiorecord.app.load.loader.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.dakare.radiorecord.app.database.table.HistoryMusicCacheTable;
import com.dakare.radiorecord.app.load.history.HistoryMusicItem;
import com.dakare.radiorecord.app.station.AbstractStation;

import java.util.ArrayList;
import java.util.List;

public class HistoryMusicCategoryDbTable extends BasicCategoryDbTable<HistoryMusicItem> {

    private final Uri uri;

    public HistoryMusicCategoryDbTable(AbstractStation station, String date) {
        uri = Uri.parse("content://com.dakare.radiorecord.app/history_music" + station.getCode() + "/" + date);
    }

    @Override
    protected List<HistoryMusicItem> fromContentValues(Cursor query) {
        List<HistoryMusicItem> result = new ArrayList<>();
        while (query.moveToNext()) {
            HistoryMusicItem historyMusicItem = new HistoryMusicItem();
            historyMusicItem.setArtist(query.getString(query.getColumnIndex(HistoryMusicCacheTable.COLUMN_ARTIST)));
            historyMusicItem.setSong(query.getString(query.getColumnIndex(HistoryMusicCacheTable.COLUMN_SONG)));
            historyMusicItem.setUrl(query.getString(query.getColumnIndex(HistoryMusicCacheTable.COLUMN_URL)));
            historyMusicItem.setWhen(query.getString(query.getColumnIndex(HistoryMusicCacheTable.COLUMN_WHEN)));
            historyMusicItem.setVisible(query.getInt(query.getColumnIndex(HistoryMusicCacheTable.COLUMN_VISIBLE)) == 1);
            result.add(historyMusicItem);
        }
        return result;
    }

    @Override
    protected Uri getUrl() {
        return uri;
    }

    @Override
    protected ContentValues[] toContentValues(List<HistoryMusicItem> result) {
        ContentValues[] values = new ContentValues[result.size()];
        for (int i = 0; i < result.size(); i++) {
            ContentValues contentValues = new ContentValues();
            HistoryMusicItem historyMusicItem = result.get(i);
            contentValues.put(HistoryMusicCacheTable.COLUMN_WHEN, historyMusicItem.getWhen());
            contentValues.put(HistoryMusicCacheTable.COLUMN_ARTIST, historyMusicItem.getArtist());
            contentValues.put(HistoryMusicCacheTable.COLUMN_SONG, historyMusicItem.getSong());
            contentValues.put(HistoryMusicCacheTable.COLUMN_URL, historyMusicItem.getUrl());
            contentValues.put(HistoryMusicCacheTable.COLUMN_VISIBLE, historyMusicItem.isVisible());
            values[i] = contentValues;
        }
        return values;
    }

}

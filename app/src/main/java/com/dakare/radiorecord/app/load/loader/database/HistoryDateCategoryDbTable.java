package com.dakare.radiorecord.app.load.loader.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.database.table.HistoryDateCacheTable;

import java.util.ArrayList;
import java.util.List;

public class HistoryDateCategoryDbTable extends BasicCategoryDbTable<String> {

    private final Uri uri;

    public HistoryDateCategoryDbTable(Station station) {
        uri = Uri.withAppendedPath(Uri.parse("content://com.dakare.radiorecord.app/history_date"), station.getCodeAsParam());
    }

    @Override
    protected List<String> fromContentValues(Cursor query) {
        List<String> result = new ArrayList<>();
        while (query.moveToNext()) {
            result.add(query.getString(query.getColumnIndex(HistoryDateCacheTable.COLUMN_DATE)));
        }
        return result;
    }

    @Override
    protected Uri getUrl() {
        return uri;
    }

    @Override
    protected ContentValues[] toContentValues(List<String> result) {
        ContentValues[] values = new ContentValues[result.size()];
        for (int i = 0; i < result.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(HistoryDateCacheTable.COLUMN_DATE, result.get(i));
            values[i] = contentValues;
        }
        return values;
    }

}

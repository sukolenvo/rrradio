package com.dakare.radiorecord.app.load.loader.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.dakare.radiorecord.app.RecordApplication;

import java.util.List;

public abstract class BasicCategoryDbTable<T> implements CategoryDbTable<T> {

    private ContentResolver contentResolver;

    public BasicCategoryDbTable() {
        contentResolver = RecordApplication.getInstance().getContentResolver();
    }

    @Override
    public List<T> load() {
        Cursor cursor = contentResolver.query(getUrl(), null, null, null, null);
        try {
            return fromContentValues(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    protected abstract List<T> fromContentValues(Cursor query);

    protected abstract Uri getUrl();

    @Override
    public void clear() {
        contentResolver.delete(getUrl(), null, null);
    }

    @Override
    public void save(List<T> result) {
        if (result != null && !result.isEmpty()) {
            contentResolver.bulkInsert(getUrl(), toContentValues(result));
        }
    }

    protected abstract ContentValues[] toContentValues(List<T> result);
}

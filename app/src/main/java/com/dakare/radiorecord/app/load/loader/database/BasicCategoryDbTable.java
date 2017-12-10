package com.dakare.radiorecord.app.load.loader.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.load.loader.CategoryResponse;

import java.util.Date;
import java.util.List;

public abstract class BasicCategoryDbTable<T> implements CategoryDbTable<T> {

    public static final String COLUMN_FROM_DATE = "from_date";

    private ContentResolver contentResolver;

    public BasicCategoryDbTable() {
        contentResolver = RecordApplication.getInstance().getContentResolver();
    }

    @Override
    public CategoryResponse<T> load() {
        Cursor cursor = contentResolver.query(getUrl(), null, null, null, null);
        try {
            if (cursor == null || cursor.getCount() == 0) {
                return CategoryResponse.emptyRespose();
            }
            List<T> data = fromContentValues(cursor);
            return CategoryResponse.createCachedResponse(getFrom(cursor), data);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private Date getFrom(Cursor cursor) {
        if (cursor.moveToFirst() && cursor.getColumnIndex(COLUMN_FROM_DATE) >= 0) {
            return new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_FROM_DATE)));
        }
        return null;
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
            ContentValues[] contentValues = toContentValues(result);
            for (ContentValues contentValue : contentValues) {
                contentValue.put(COLUMN_FROM_DATE, System.currentTimeMillis());
            }
            contentResolver.bulkInsert(getUrl(), contentValues);
        }
    }

    protected abstract ContentValues[] toContentValues(List<T> result);
}

package com.dakare.radiorecord.app.database.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SectionPathCacheTable extends Table {

    public static final String NAME = "section_path";

    public static final String CREATE_TABLE = "create table " + NAME + " (_id TEXT PRIMARY KEY, url TEXT NOT NULL)";

    public static final String DROP_TABLE = "drop table if exists " + NAME;

    public static final String COLUMN_CATEGORY_NAME = "_id";
    public static final String COLUMN_CATEGORY_URL = "url";

    public SectionPathCacheTable(SQLiteDatabase database) {
        super(database);
    }

    public int bulkSave(final ContentValues[] contentValues) {

        for (int i = 0; i < contentValues.length; i += 500) {
            getDatabase().beginTransaction();
            try {
                for (int j = i; j < contentValues.length && j < i + 500; j++) {
                    ContentValues contentValue = contentValues[j];
                    getDatabase().insert(NAME, null, contentValue);
                }
                getDatabase().setTransactionSuccessful();
            } finally {
                getDatabase().endTransaction();
            }
        }
        return contentValues.length;
    }

    public Cursor find(final String[] projection, final String selection,
                       final String[] selectionArgs, final String sortOrder) {
        return getDatabase().query(NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public int delete(final String where, final String[] whereArgs) {
        return getDatabase().delete(NAME, where, whereArgs);
    }
}

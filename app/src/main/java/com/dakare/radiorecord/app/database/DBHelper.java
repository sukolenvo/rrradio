package com.dakare.radiorecord.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.dakare.radiorecord.app.database.table.*;

public class DBHelper extends SQLiteOpenHelper {

    @Deprecated
    private static final int DB_VERSION_1 = 1;
    @Deprecated
    private static final int DB_VERSION_2 = 2;
    private static final int DB_VERSION_3 = 3;
    private static final String NAME = "radiorecord.db";

    public DBHelper(final Context context) {
        super(context, NAME, null, DB_VERSION_3);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(DownloadAudioTable.CREATE_TABLE);
        db.execSQL(SectionPathCacheTable.CREATE_TABLE);
        db.execSQL(SectionMusicCacheTable.CREATE_TABLE);
        db.execSQL(TopsCacheTable.CREATE_TABLE);
        db.execSQL(HistoryDateCacheTable.CREATE_TABLE);
        db.execSQL(HistoryMusicCacheTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        if (oldVersion == DB_VERSION_1) {
            db.execSQL("ALTER TABLE " + DownloadAudioTable.NAME + " add column file TEXT");
        }
        if (oldVersion == DB_VERSION_2) {
            db.execSQL(SectionPathCacheTable.CREATE_TABLE);
            db.execSQL(SectionMusicCacheTable.CREATE_TABLE);
            db.execSQL(HistoryDateCacheTable.CREATE_TABLE);
            db.execSQL(HistoryMusicCacheTable.CREATE_TABLE);
            db.execSQL(TopsCacheTable.CREATE_TABLE);
        }
    }

}

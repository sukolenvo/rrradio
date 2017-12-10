package com.dakare.radiorecord.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.crashlytics.android.Crashlytics;
import com.dakare.radiorecord.app.database.table.*;
import com.dakare.radiorecord.app.load.loader.BasicCategoryLoader;
import com.dakare.radiorecord.app.load.loader.SectionCategoryLoader;
import com.dakare.radiorecord.app.load.loader.database.BasicCategoryDbTable;

public class DBHelper extends SQLiteOpenHelper {

    @Deprecated
    private static final int DB_VERSION_1 = 1;
    @Deprecated
    private static final int DB_VERSION_2 = 2;
    @Deprecated
    private static final int DB_VERSION_3 = 3;
    private static final int DB_VERSION_4 = 4;
    private static final String NAME = "radiorecord.db";

    public DBHelper(final Context context) {
        super(context, NAME, null, DB_VERSION_4);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        try {
            db.execSQL(DownloadAudioTable.CREATE_TABLE);
            db.execSQL(SectionPathCacheTable.CREATE_TABLE);
            db.execSQL(SectionMusicCacheTable.CREATE_TABLE);
            db.execSQL(TopsCacheTable.CREATE_TABLE);
            db.execSQL(HistoryDateCacheTable.CREATE_TABLE);
            db.execSQL(HistoryMusicCacheTable.CREATE_TABLE);
        } catch (Exception e) {
            Crashlytics.logException(e);
            throw e;
        }
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        try {
            if (oldVersion == DB_VERSION_1) {
                db.execSQL("ALTER TABLE " + DownloadAudioTable.NAME + " add column file TEXT");
            }
            if (oldVersion <= DB_VERSION_2) {
                db.execSQL(SectionPathCacheTable.CREATE_TABLE);
                db.execSQL(SectionMusicCacheTable.CREATE_TABLE);
                db.execSQL(HistoryDateCacheTable.CREATE_TABLE);
                db.execSQL(HistoryMusicCacheTable.CREATE_TABLE);
                db.execSQL(TopsCacheTable.CREATE_TABLE);
            }
            if (oldVersion <= DB_VERSION_3) {
                db.execSQL(SectionPathCacheTable.DROP_TABLE);
                db.execSQL(SectionMusicCacheTable.DROP_TABLE);
                db.execSQL(HistoryDateCacheTable.DROP_TABLE);
                db.execSQL(HistoryMusicCacheTable.DROP_TABLE);
                db.execSQL(TopsCacheTable.DROP_TABLE);
                db.execSQL(SectionPathCacheTable.CREATE_TABLE);
                db.execSQL(SectionMusicCacheTable.CREATE_TABLE);
                db.execSQL(HistoryDateCacheTable.CREATE_TABLE);
                db.execSQL(HistoryMusicCacheTable.CREATE_TABLE);
                db.execSQL(TopsCacheTable.CREATE_TABLE);
            }
        } catch (Exception e) {
            throw e;
        }
    }

}

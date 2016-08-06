package com.dakare.radiorecord.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION_1 = 1;
    private static final String NAME = "radiorecord.db";

    public DBHelper(final Context context) {
        super(context, NAME, null, DB_VERSION_1);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(DownloadAudioTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        //No action yet
    }

}

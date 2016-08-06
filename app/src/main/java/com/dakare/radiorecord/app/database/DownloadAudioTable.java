package com.dakare.radiorecord.app.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dakare.radiorecord.app.R;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class DownloadAudioTable extends Table {

    private static final String NAME = "download_audio_table";
    public static final String DEFAULT_DIRECTORY_NAME = "Record Music";

    public static final String CREATE_TABLE = "create table " + NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, "
            + "subtitle TEXT, url TEXT not null, directory TEXT NOT NULL, status INTEGER DEFAULT 2, saved INTEGER NOT NULL, size INTEGER DEFAULT 0," +
            "total INTEGER DEFAULT 0)";

    public static final String DROP_TABLE = "drop table if exists " + NAME;

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_SUBTITLE = "subtitle";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_DIRECTORY = "directory";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_SAVED_DATE = "saved";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_TOTAL_SIZE = "total";

    public DownloadAudioTable(SQLiteDatabase database) {
        super(database);
    }

    public long save(final ContentValues contentValues) {
        return getDatabase().insert(NAME, null, contentValues);
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

    public void updateById(final int id, final ContentValues values) {
        getDatabase().update(NAME, values, "_id=?", new String[]{String.valueOf(id)});
    }

    public int delete(final String where, final String[] whereArgs) {
        return getDatabase().delete(NAME, where, whereArgs);
    }

    @AllArgsConstructor
    @Getter
    public enum Status {
        DOWNLOADED(0, R.string.status_audio_downloaded),
        DOWNLOADING(1, R.string.status_audio_downloading),
        NONE(2, R.string.status_audio_none),
        ERROR_BROKEN_URL(3, R.string.status_error_url),
        ERROR_FILE_MISSING(4, R.string.status_error_file),
        ERROR_SAVING(5, R.string.status_error_saving),
        ERROR_UNKNOWN(6, R.string.status_error_unknown);

        private final int code;
        private final int messageId;

        public static Status getByCode(final int code) {
            for (Status status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No status for code " + code);
        }

        public static Status getFromCursor(final Cursor cursor) {
            return getByCode(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
        }
    }
}

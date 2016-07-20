package com.dakare.radiorecord.app.database.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.dakare.radiorecord.app.database.DBHelper;
import com.dakare.radiorecord.app.database.DownloadAudioTable;

public class StorageProvider extends ContentProvider {
	
	private SQLiteDatabase dataBase;
    private DownloadAudioTable downloadAudioTable;

	@Override
	public boolean onCreate() {
		dataBase = new DBHelper(getContext()).getWritableDatabase();
        downloadAudioTable = new DownloadAudioTable(dataBase);
		return true;
	}

	@Override
	public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        switch (StorageUriMatcher.matchUri(uri))
        {
            case StorageUriMatcher.DOWNLOAD_AUDIO_URI:
                return downloadAudioTable.delete(selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot insert values with uri " + uri);
        }
	}

	@Override
	public String getType(Uri uri) {
		return "android.cursor.item/com.dakare.radiorecord.record";
	}

	@Override
	public Uri insert(final Uri uri, final ContentValues values) {
        switch (StorageUriMatcher.matchUri(uri))
        {
            case StorageUriMatcher.DOWNLOAD_AUDIO_URI:
                return Uri.withAppendedPath(uri, downloadAudioTable.save(values) + "");
            default:
                throw new IllegalArgumentException("Cannot insert values with uri " + uri);
        }
	}
	
	@Override
	public int bulkInsert(final Uri uri, final ContentValues[] values) {
        switch (StorageUriMatcher.matchUri(uri))
        {
            case StorageUriMatcher.DOWNLOAD_AUDIO_URI:
                return downloadAudioTable.bulkSave(values);
            default:
                throw new IllegalArgumentException("Cannot perform bulk insert for uri " + uri);
        }
	}

	@Override
	public Cursor query(final Uri uri, final String[] projection, final String selection,
			final String[] selectionArgs, final String sortOrder) {
        switch (StorageUriMatcher.matchUri(uri))
        {
            case StorageUriMatcher.DOWNLOAD_AUDIO_URI:
                return downloadAudioTable.find(projection, selection, selectionArgs, sortOrder);
            default:
                throw new IllegalArgumentException("Cannot perform query for uri " + uri);
        }
	}

	@Override
	public int update(final Uri uri, final ContentValues values, final String selection,
			final String[] selectionArgs) {
        switch (StorageUriMatcher.matchUri(uri))
        {
            case StorageUriMatcher.DOWNLOAD_AUDIO_ITEM_URI:
                downloadAudioTable.updateById(Integer.parseInt(uri.getLastPathSegment()), values);
                return 1;
            default:
                throw new IllegalArgumentException("Cannot perform update for uri " + uri);
        }
	}

	@Override
	public void shutdown() {
		super.shutdown();
        dataBase.close();
	}
}

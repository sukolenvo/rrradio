package com.dakare.radiorecord.app.database.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.database.DownloadAudioTable;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;

import java.util.List;

public class StorageContract {
    private final ContentResolver resolver;
    private final Uri baseUri;
    private final PreferenceManager preferenceManager;
    private final Uri downloadAudioUri;

    private StorageContract() {
        resolver = RecordApplication.getInstance().getContentResolver();
        baseUri = Uri.parse("content://com.dakare.radiorecord.app");
        preferenceManager = PreferenceManager.getInstance(RecordApplication.getInstance());
        downloadAudioUri = Uri.withAppendedPath(baseUri, "download_audio");
    }

    public static StorageContract getInstance() {
        return new StorageContract();
    }

    public long insertDownloadAudio(final PlaylistItem item) {
        return Long.parseLong(resolver.insert(downloadAudioUri, toContentValues(item)).getLastPathSegment());
    }

    public long insertRecordingAudio(final PlaylistItem item, final String fileName) {
        ContentValues values = toContentValues(item);
        values.put(DownloadAudioTable.COLUMN_STATUS, DownloadAudioTable.Status.DOWNLOADED.name());
        values.put(DownloadAudioTable.COLUMN_FILE_NAME, fileName);
        return Long.parseLong(resolver.insert(downloadAudioUri, values).getLastPathSegment());
    }

    public void updateAudioStatus(final long id, final DownloadAudioTable.Status status) {
        ContentValues values = new ContentValues();
        values.put(DownloadAudioTable.COLUMN_STATUS, status.getCode());
        resolver.update(Uri.withAppendedPath(downloadAudioUri, String.valueOf(id)), values, null, null);
    }

    public void updateAudio(final long id, final ContentValues contentValues) {
        resolver.update(Uri.withAppendedPath(downloadAudioUri, String.valueOf(id)), contentValues, null, null);
    }

    private ContentValues toContentValues(final PlaylistItem item) {
        ContentValues values = new ContentValues();
        values.put(DownloadAudioTable.COLUMN_TITLE, item.getTitle());
        values.put(DownloadAudioTable.COLUMN_SUBTITLE, item.getSubtitle());
        values.put(DownloadAudioTable.COLUMN_URL, item.getUrl());
        values.put(DownloadAudioTable.COLUMN_STATUS, DownloadAudioTable.Status.NONE.getCode());
        values.put(DownloadAudioTable.COLUMN_DIRECTORY, preferenceManager.getDownloadDirectory());
        values.put(DownloadAudioTable.COLUMN_SAVED_DATE, System.currentTimeMillis());
        return values;
    }

    public void bulkInsertDownloadAudio(final List<PlaylistItem> items) {
        ContentValues[] values = new ContentValues[items.size()];
        for (int i = 0; i < items.size(); i++) {
            values[i] = toContentValues(items.get(i));
        }
        resolver.bulkInsert(downloadAudioUri, values);
    }

    public Cursor getAllAudio() {
        return resolver.query(downloadAudioUri, null, null, null, preferenceManager.getDownloadsSort().getSort());
    }

    public Cursor getAudioToDownload() {
        return resolver.query(downloadAudioUri, null, "status = ? OR status = ? ", new String[]{String.valueOf(DownloadAudioTable.Status.NONE.getCode()),
                String.valueOf(DownloadAudioTable.Status.DOWNLOADING.getCode())}, preferenceManager.getDownloadsSort().getSort());
    }

    public Cursor getAudioByIds(final List<Integer> ids) {
        return resolver.query(downloadAudioUri, null, DownloadAudioTable.COLUMN_ID + " in (" + joinList(ids) + ")", null, null);
    }

    private String joinList(final List<?> items) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Object item : items) {
            if (!first) {
                builder.append(",");
            }
            first = false;
            builder.append(item);
        }
        return builder.toString();
    }

    public void deleteAudio(final List<Long> ids) {
        resolver.delete(downloadAudioUri, DownloadAudioTable.COLUMN_ID + " in (" + joinList(ids) + ")", null);
    }
}

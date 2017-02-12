package com.dakare.radiorecord.app.download.service;

import android.database.Cursor;
import android.net.Uri;
import com.dakare.radiorecord.app.database.DownloadAudioTable;
import lombok.Data;

import java.io.File;

@Data
public class DownloadItem {

    private final long id;
    private final String url;
    private final String directory;
    private final String title;
    private final String subtitle;
    private long size;
    private long totalSize;
    private final long saved;
    private final String fileName;
    private DownloadAudioTable.Status status;

    public DownloadItem(final Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(DownloadAudioTable.COLUMN_ID));
        url = cursor.getString(cursor.getColumnIndex(DownloadAudioTable.COLUMN_URL));
        title = cursor.getString(cursor.getColumnIndex(DownloadAudioTable.COLUMN_TITLE));
        subtitle = cursor.getString(cursor.getColumnIndex(DownloadAudioTable.COLUMN_SUBTITLE));
        directory = cursor.getString(cursor.getColumnIndex(DownloadAudioTable.COLUMN_DIRECTORY));
        size = cursor.getLong(cursor.getColumnIndex(DownloadAudioTable.COLUMN_SIZE));
        totalSize = cursor.getLong(cursor.getColumnIndex(DownloadAudioTable.COLUMN_TOTAL_SIZE));
        saved = cursor.getLong(cursor.getColumnIndex(DownloadAudioTable.COLUMN_SAVED_DATE));
        status = DownloadAudioTable.Status.getFromCursor(cursor);
        fileName = cursor.getString(cursor.getColumnIndex(DownloadAudioTable.COLUMN_FILE_NAME));
    }

    public File getFile() {
        if (fileName == null) {
            return DownloadManager.getAudioFile(directory, id, title);
        }
        return new File(new File(directory), fileName);
    }

    public Uri getFileUri() {
        return Uri.fromFile(getFile());
    }
}

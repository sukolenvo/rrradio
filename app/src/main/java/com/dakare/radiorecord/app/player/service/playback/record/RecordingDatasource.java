package com.dakare.radiorecord.app.player.service.playback.record;

import android.content.ContentValues;
import android.util.Log;
import android.widget.Toast;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.database.DownloadAudioTable;
import com.dakare.radiorecord.app.database.provider.StorageContract;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DataSpec;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RecordingDatasource implements DataSource {

    private final File file;
    private final DataSource wrapped;
    private OutputStream destination;
    private final long id;
    private int ready;

    public RecordingDatasource(final DataSource wrapped, final File file, final long id) {
        this.wrapped = wrapped;
        this.file = file;
        this.id = id;
        try {
            this.destination = new BufferedOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            Toast.makeText(RecordApplication.getInstance(), "Cannot create file for saving record. Please check write permission to destination folder.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public long open(final DataSpec dataSpec) throws IOException {
        return wrapped.open(dataSpec);
    }

    @Override
    public void close() throws IOException {
        wrapped.close();
    }

    @Override
    public int read(final byte[] buffer, final int offset, final int readLength) throws IOException {
        int read = wrapped.read(buffer, offset, readLength);
        if (read > 0 && destination != null) {
            destination.write(buffer, offset, read);
            ready += read;
            if (ready > 4096) {
                updateSize();
            }
        }
        return read;
    }

    public void updateSize() {
        if (destination != null) {
            try {
                destination.flush();
            } catch (IOException e) {
                Log.e("RecordingDatasource", "Error flushing record", e);
            }
        }
        long size = file.length();
        ContentValues values = new ContentValues();
        values.put(DownloadAudioTable.COLUMN_SIZE, size);
        values.put(DownloadAudioTable.COLUMN_TOTAL_SIZE, size);
        StorageContract.getInstance().updateAudio(id, values);
        ready = 0;
    }

    public void stop() {
        updateSize();
        try {
            if (destination != null) {
                destination.close();
            }
        } catch (IOException e) {
            //Nothing to do
        }
    }
}

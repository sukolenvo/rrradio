package com.dakare.radiorecord.app.player.service.playback.record;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.database.provider.StorageContract;
import com.dakare.radiorecord.app.database.table.DownloadAudioTable;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;

import java.io.*;

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
            Toast.makeText(RecordApplication.getInstance(), R.string.error_recording, Toast.LENGTH_LONG).show();
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

    @Override
    public Uri getUri() {
        return wrapped.getUri();
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

package com.dakare.radiorecord.app.player.service.playback.record;

import com.google.android.exoplayer2.upstream.DataSource;

import java.io.File;

public class RecordingDatasourceFactory implements DataSource.Factory {

    private final DataSource.Factory wrapped;
    private final File file;
    private final long id;
    private RecordingDatasource recordingDatasource;

    public RecordingDatasourceFactory(DataSource.Factory wrapped, File file, long id) {
        this.wrapped = wrapped;
        this.file = file;
        this.id = id;
    }

    @Override
    public DataSource createDataSource() {
        stop();
        recordingDatasource = new RecordingDatasource(wrapped.createDataSource(), file, id);
        return recordingDatasource;
    }

    public void stop() {
        if (recordingDatasource != null) {
            recordingDatasource.stop();
        }
    }
}

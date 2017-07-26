package com.dakare.radiorecord.app.player.service.playback.record;

import android.content.Context;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.database.provider.StorageContract;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.google.android.exoplayer2.upstream.DataSource;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

public class PlaybackRecordManager {

    private final Context context;
    private final File destination;
    private RecordingDatasourceFactory recordingDatasource;
    @Setter
    @Getter
    private boolean record;

    public PlaybackRecordManager(final Context context) {
        this.context = context;
        destination = new File(PreferenceManager.getInstance(context).getDownloadDirectory());
    }

    public DataSource.Factory startRecording(final PlaylistItem item, final DataSource.Factory dataSource) {
        if (record && item.isLive()) {
            try {
                destination.mkdirs();
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
            PlaylistItem fileItem = new PlaylistItem(item);
            String fileName = getFileName(item);
            fileItem.setTitle(fileName);
            long id = StorageContract.getInstance().insertRecordingAudio(fileItem, fileName);
            recordingDatasource = new RecordingDatasourceFactory(dataSource, new File(destination, fileName), id);
            Toast.makeText(context, context.getString(R.string.recording_starting_info, fileName), Toast.LENGTH_LONG).show();
            return recordingDatasource;
        }
        return dataSource;
    }

    private String getFileName(final PlaylistItem item) {
        String extension = item.getUrl().contains("aac") ? ".aac" : ".mp3";
        String name = item.getStation().getName();
        if (new File(destination, name + extension).exists()) {
            for (int i = 1; ; i++) {
                String newName = name + "(" + i + ")" + extension;
                if (!new File(destination, newName).exists()) {
                    return newName;
                }
            }
        }
        return name + extension;
    }

    public void stop() {
        if (recordingDatasource != null) {
            Toast.makeText(context, R.string.recording_stopped_info, Toast.LENGTH_LONG).show();
            recordingDatasource.stop();
            recordingDatasource = null;
        }
        record = false;
    }
}

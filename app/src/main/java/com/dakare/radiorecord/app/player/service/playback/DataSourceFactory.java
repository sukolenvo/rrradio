package com.dakare.radiorecord.app.player.service.playback;

import android.widget.Toast;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.FileDataSource;

public final class DataSourceFactory {

    public static DataSource createDataSource(final String url) {
        if (url.startsWith("file://")) {
            return new FileDataSource();
        }
        if (url.startsWith("http")) {
            return new PartialHttpDataSource("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36", null);
        }
        Toast.makeText(RecordApplication.getInstance(), R.string.error_source, Toast.LENGTH_LONG).show();
        return null;
    }
}

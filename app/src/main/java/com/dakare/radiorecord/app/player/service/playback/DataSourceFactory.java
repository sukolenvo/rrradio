package com.dakare.radiorecord.app.player.service.playback;

import android.widget.Toast;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.load.AbstractLoadFragment;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.FileDataSource;

public final class DataSourceFactory {

    public static DataSource createDataSource(final String url, final boolean stream) {
        if (url.startsWith("file://")) {
            return new FileDataSource();
        }
        if (url.startsWith("http")) {
            return new PartialHttpDataSource(AbstractLoadFragment.USER_AGENT, null, stream);
        }
        Toast.makeText(RecordApplication.getInstance(), R.string.error_source, Toast.LENGTH_LONG).show();
        return null;
    }
}

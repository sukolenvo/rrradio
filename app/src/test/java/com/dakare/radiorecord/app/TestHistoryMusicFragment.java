package com.dakare.radiorecord.app;

import android.annotation.SuppressLint;
import com.dakare.radiorecord.app.load.history.HistoryMusicItem;
import com.dakare.radiorecord.app.load.history.HistoryMusicSelectFragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SuppressLint("ValidFragment")
public class TestHistoryMusicFragment extends HistoryMusicSelectFragment {

    public TestHistoryMusicFragment(final Station station) {
        super();
        ReflectionUtils.setField(this, "station", station);
        ReflectionUtils.setField(this, "date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }

    @Override
    protected boolean isDestroyed() {
        return false;
    }

    @Override
    protected void setStatus(final int statusId, final Object... args) {
    }

    public List<HistoryMusicItem> load() throws IOException {
        return startLoading();
    }
}

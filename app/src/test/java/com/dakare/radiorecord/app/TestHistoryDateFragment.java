package com.dakare.radiorecord.app;

import android.annotation.SuppressLint;
import com.dakare.radiorecord.app.load.history.HistoryDateSelectFragment;

import java.io.IOException;
import java.util.List;

@SuppressLint("ValidFragment")
public class TestHistoryDateFragment extends HistoryDateSelectFragment {

    public TestHistoryDateFragment(final Station station) {
        super();
        ReflectionUtils.setField(this, "station", station);
    }

    @Override
    protected boolean isDestroyed() {
        return false;
    }

    @Override
    protected void setStatus(final int statusId, final Object... args) {
    }

    public List<String> load() throws IOException {
        return startLoading();
    }
}

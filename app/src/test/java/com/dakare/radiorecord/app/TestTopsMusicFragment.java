package com.dakare.radiorecord.app;

import android.annotation.SuppressLint;
import com.dakare.radiorecord.app.load.top.TopsMusicFragment;
import com.dakare.radiorecord.app.load.top.TopsMusicItem;

import java.io.IOException;
import java.util.List;

@SuppressLint("ValidFragment")
public class TestTopsMusicFragment extends TopsMusicFragment {

    public TestTopsMusicFragment(final Station station) {
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

    public List<TopsMusicItem> load() throws IOException {
        return startLoading();
    }
}

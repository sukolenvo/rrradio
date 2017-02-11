package com.dakare.radiorecord.app;

import android.annotation.SuppressLint;
import com.dakare.radiorecord.app.load.section.SectionFragment;
import com.dakare.radiorecord.app.load.section.SectionMusicItem;
import com.dakare.radiorecord.app.load.top.TopsMusicFragment;
import com.dakare.radiorecord.app.load.top.TopsMusicItem;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

@SuppressLint("ValidFragment")
public class TestTopsMusicFragment extends TopsMusicFragment {

    public TestTopsMusicFragment(final Station station) {
        super();
        Field categoryName = ReflectionUtils.findField(TopsMusicFragment.class, "station");
        categoryName.setAccessible(true);
        ReflectionUtils.setField(categoryName, this, station);
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

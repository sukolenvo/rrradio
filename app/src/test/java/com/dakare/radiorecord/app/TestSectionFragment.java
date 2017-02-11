package com.dakare.radiorecord.app;

import android.annotation.SuppressLint;
import com.dakare.radiorecord.app.load.section.SectionFragment;
import com.dakare.radiorecord.app.load.section.SectionMusicItem;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

@SuppressLint("ValidFragment")
public class TestSectionFragment extends SectionFragment {

    public TestSectionFragment(final String category) {
        super();
        Field categoryName = ReflectionUtils.findField(SectionFragment.class, "categoryName");
        categoryName.setAccessible(true);
        ReflectionUtils.setField(categoryName, this, category);
    }

    @Override
    protected boolean isDestroyed() {
        return false;
    }

    @Override
    protected void setStatus(final int statusId, final Object... args) {
    }

    public List<SectionMusicItem> load() throws IOException {
        return startLoading();
    }
}

package com.dakare.radiorecord.app;

import android.annotation.SuppressLint;
import com.dakare.radiorecord.app.load.history.HistoryDateSelectFragment;
import com.dakare.radiorecord.app.load.history.HistoryMusicItem;
import com.dakare.radiorecord.app.load.history.HistoryMusicSelectFragment;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SuppressLint("ValidFragment")
public class TestHistoryMusicFragment extends HistoryMusicSelectFragment {

    public TestHistoryMusicFragment(final Station station) {
        super();
        Field categoryName = ReflectionUtils.findField(HistoryMusicSelectFragment.class, "station");
        categoryName.setAccessible(true);
        ReflectionUtils.setField(categoryName, this, station);
        Field dateField = ReflectionUtils.findField(HistoryMusicSelectFragment.class, "date");
        dateField.setAccessible(true);
        ReflectionUtils.setField(dateField, this, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
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

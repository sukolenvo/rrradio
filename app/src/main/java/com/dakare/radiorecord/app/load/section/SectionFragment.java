package com.dakare.radiorecord.app.load.section;

import android.os.Bundle;
import com.dakare.radiorecord.app.load.loader.CategoryLoader;
import com.dakare.radiorecord.app.load.loader.SectionCategoryLoader;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionAdapter;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionFragment;

public class SectionFragment extends AbstractSelectionFragment<SectionAdapter.ViewHolder, SectionMusicItem> {
    public static final String CATEGORY_KEY = "category";

    private String categoryName;
    private SectionAdapter adapter;
    private CategoryLoader<SectionMusicItem> loader;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        categoryName = getArguments().getString(CATEGORY_KEY);
        super.onCreate(savedInstanceState);
        adapter = new SectionAdapter(getContext(), getSelectionManager(), this);
        loader = new SectionCategoryLoader(categoryName);
    }

    @Override
    protected AbstractSelectionAdapter<SectionAdapter.ViewHolder, SectionMusicItem> getAdapter() {
        return adapter;
    }

    @Override
    protected CategoryLoader<SectionMusicItem> getCategoryLoader() {
        return loader;
    }

}

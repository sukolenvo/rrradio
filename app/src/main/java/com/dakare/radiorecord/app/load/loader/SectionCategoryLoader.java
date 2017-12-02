package com.dakare.radiorecord.app.load.loader;

import com.dakare.radiorecord.app.load.loader.database.SectionMusicCategoryDbTable;
import com.dakare.radiorecord.app.load.loader.database.SectionPathCategoryDbTable;
import com.dakare.radiorecord.app.load.loader.parser.SectionMusicParser;
import com.dakare.radiorecord.app.load.loader.parser.SectionPathParser;
import com.dakare.radiorecord.app.load.section.SectionMusicItem;
import com.dakare.radiorecord.app.load.section.SectionPath;

import java.util.Collections;
import java.util.List;

public class SectionCategoryLoader implements CategoryLoader<SectionMusicItem> {

    private static final String CATEGORY_LIST_URL = "http://www.radiorecord.ru/pda_new/superchart";

    private final CategoryLoader<SectionPath> sectionPathLoader;
    private final BasicCategoryLoader<SectionMusicItem> sectionMusicLoader;
    private final String sectionName;
    private boolean canceled;

    public SectionCategoryLoader(String sectionName) {
        this.sectionName = sectionName;
        sectionPathLoader = new BasicCategoryLoader<>(new SectionPathCategoryDbTable(), new SectionPathParser(), CATEGORY_LIST_URL);
        sectionMusicLoader = new BasicCategoryLoader<>(new SectionMusicCategoryDbTable(sectionName), new SectionMusicParser());
    }

    @Override
    public void load(final CategoryLoadListener<SectionMusicItem> listener) {
        sectionPathLoader.load(new CategoryLoadListener<SectionPath>() {
            @Override
            public void onCategoryLoaded(CategoryResponse<SectionPath> networkResult) {
                if (canceled) {
                    return;
                }
                String url = null;
                for (SectionPath sectionPath : networkResult.getData()) {
                    if (sectionName.equals(sectionPath.getName())) {
                        url = sectionPath.getUrl();
                    }
                }
                if (url == null) {
                    listener.onCategoryLoaded(CategoryResponse.<SectionMusicItem>emptyRespose());
                } else {
                    sectionMusicLoader.setUrl(url);
                    sectionMusicLoader.load(listener);
                }
            }
        });
    }

    @Override
    public void clearCache() {
        sectionPathLoader.clearCache();
        sectionMusicLoader.clearCache();
    }

    @Override
    public void cancel() {
        sectionPathLoader.cancel();
        sectionMusicLoader.cancel();
        canceled = true;
    }
}

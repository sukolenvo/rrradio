package com.dakare.radiorecord.app.load.loader.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.dakare.radiorecord.app.database.table.SectionPathCacheTable;
import com.dakare.radiorecord.app.load.section.SectionPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SectionPathCategoryDbTable extends BasicCategoryDbTable<SectionPath> {

    private static final Uri RESOLVER_URI = Uri.parse("content://com.dakare.radiorecord.app/section_path");

    @Override
    protected List<SectionPath> fromContentValues(Cursor query) {
        if (query == null) {
            return Collections.emptyList();
        }
        List<SectionPath> result = new ArrayList<>();
        while (query.moveToNext()) {
            SectionPath item = new SectionPath();
            item.setName(query.getString(query.getColumnIndex(SectionPathCacheTable.COLUMN_CATEGORY_NAME)));
            item.setUrl(query.getString(query.getColumnIndex(SectionPathCacheTable.COLUMN_CATEGORY_URL)));
            result.add(item);
        }
        return result;
    }

    @Override
    protected Uri getUrl() {
        return RESOLVER_URI;
    }

    @Override
    protected ContentValues[] toContentValues(List<SectionPath> result) {
        ContentValues[] values = new ContentValues[result.size()];
        for (int i = 0; i < result.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SectionPathCacheTable.COLUMN_CATEGORY_NAME, result.get(i).getName());
            contentValues.put(SectionPathCacheTable.COLUMN_CATEGORY_URL, result.get(i).getUrl());
            values[i] = contentValues;
        }
        return values;
    }

}

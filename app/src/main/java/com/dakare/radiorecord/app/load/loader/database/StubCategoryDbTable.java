package com.dakare.radiorecord.app.load.loader.database;

import java.util.Collections;
import java.util.List;

public class StubCategoryDbTable<T> implements CategoryDbTable<T> {

    @Override
    public List<T> load() {
        return Collections.emptyList();
    }

    @Override
    public void clear() {

    }

    @Override
    public void save(List<T> result) {

    }
}

package com.dakare.radiorecord.app.load.loader.database;

import com.dakare.radiorecord.app.load.loader.CategoryResponse;

import java.util.List;

public interface CategoryDbTable<T> {

    CategoryResponse<T> load();

    void clear();

    void save(List<T> result);
}

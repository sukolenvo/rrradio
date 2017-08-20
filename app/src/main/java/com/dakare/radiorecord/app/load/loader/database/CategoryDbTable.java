package com.dakare.radiorecord.app.load.loader.database;

import java.util.List;

public interface CategoryDbTable<T> {

    List<T> load();

    void clear();

    void save(List<T> result);
}

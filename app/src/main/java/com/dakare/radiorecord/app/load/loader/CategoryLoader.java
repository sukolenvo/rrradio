package com.dakare.radiorecord.app.load.loader;

public interface CategoryLoader<T> {

    void load(CategoryLoadListener<T> listener);

    void clearCache();

    void cancel();
}

package com.dakare.radiorecord.app.load.loader;

public interface CategoryLoadListener<T> {

    void onCategoryLoaded(CategoryResponse<T> networkResult);
}

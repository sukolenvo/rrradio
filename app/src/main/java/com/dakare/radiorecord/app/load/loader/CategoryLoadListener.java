package com.dakare.radiorecord.app.load.loader;

import java.util.List;

public interface CategoryLoadListener<T> {

    void onCategoryLoaded(List<T> networkResult);
}

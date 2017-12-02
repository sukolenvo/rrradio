/*
 * Copyright (c) 2017 Practice Insight Pty Ltd. All rights reserved.
 */

package com.dakare.radiorecord.app.load.loader;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author vadym
 */
@Data
@AllArgsConstructor
@SuppressWarnings("unchecked")
public class CategoryResponse<T> {

    private static final CategoryResponse EMPTY = new CategoryResponse(false, null, Collections.emptyList());

    private final boolean cache;
    private final Date from;
    private final List<T> data;

    public static <Type> CategoryResponse<Type> createNetworkResponse(List<Type> response) {
        return new CategoryResponse<>(false, null, response);
    }

    public static <Type> CategoryResponse<Type> createCachedResponse(Date from, List<Type> data) {
        return new CategoryResponse<>(true, from, data);
    }

    public static <Type> CategoryResponse<Type> emptyRespose() {
        return EMPTY;
    }
}

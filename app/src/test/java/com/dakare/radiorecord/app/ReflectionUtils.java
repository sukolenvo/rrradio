package com.dakare.radiorecord.app;

import java.lang.reflect.Field;

public class ReflectionUtils {

    public static void setField(Object o, String fieldName, Object value) {
        try {
            Field declaredField = getFieldRecursive(o.getClass(), fieldName);
            declaredField.setAccessible(true);
            declaredField.set(o, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Field getFieldRecursive(Class clazz, String field) {
        try {
            return clazz.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            return getFieldRecursive(clazz.getSuperclass(), field);
        }
    }
}

package com.blogspot.sontx.bottle.utils;

public final class StringUtils {

    public static boolean isEmpty(String st) {
        return st == null || st.trim().length() == 0;
    }

    private StringUtils() {
    }
}

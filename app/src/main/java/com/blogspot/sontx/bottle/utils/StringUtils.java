package com.blogspot.sontx.bottle.utils;

import java.util.UUID;

public final class StringUtils {

    public static boolean isEmpty(String st) {
        return st == null || st.trim().length() == 0;
    }

    public static String randomString() {
        return UUID.randomUUID().toString();
    }

    private StringUtils() {
    }
}

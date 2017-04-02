package it.slyce.messaging.utils;

public final class StringUtils {
    private StringUtils() {
    }

    public static String toUppercaseFirstCharacter(String word) {
        if (word == null || word.length() == 0)
            return word;
        if (word.length() == 1)
            return word.toUpperCase();
        return word.substring(0, 1).toUpperCase().concat(word.substring(1));
    }
}

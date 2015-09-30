package com.mikhaildev.profiru.util;


public class StringUtils {

    public static final String FRIEND_LIST_FRAGMENT = "friend_list_fragment";
    public static final String EXTRA_DATA = "extra_data";

    private StringUtils() {
        //Empty
    }

    public static boolean isNullOrEmpty(final String string) {
        return string == null || string.length()==0;
    }
}

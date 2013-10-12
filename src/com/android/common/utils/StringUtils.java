package com.android.common.utils;

/**
 * User: Gyb
 * Date: 13-9-26
 * Time: 下午3:34
 */
public class StringUtils {

    public static boolean isEmpty(String str) {
        if (!isNull(str) && !"".equals(str.trim()))
            return false;
        return true;
    }

    public static boolean isNull(String str) {
        return str == null;
    }
}

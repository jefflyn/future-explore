package com.guru.future.common.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

/**
 * @author j
 */
@Slf4j
public class NullUtil {
    public static boolean checkNullValue(Object... values) {
        for (Object object : values) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }

    public static String defaultValue(String... values) {
        for (String val : values) {
            if (val != null) {
                return val;
            }
        }
        return Strings.EMPTY;
    }
}
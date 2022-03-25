package com.guru.future.common.utils;


import lombok.extern.slf4j.Slf4j;

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
}
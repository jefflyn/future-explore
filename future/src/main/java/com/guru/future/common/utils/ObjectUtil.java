package com.guru.future.common.utils;


import lombok.extern.slf4j.Slf4j;

/**
 * @author j
 */
@Slf4j
public class ObjectUtil {
    public static boolean checkNullValue(Object... values) {
        for (Object object : values) {
            return object == null;
        }
        return true;
    }
}
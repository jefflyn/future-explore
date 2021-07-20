package com.guru.future.common.utils;


import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * @author j
 */
public class DateUtil {
    public static String currentTradeDate() {
        Date date = new Date();
        return DateFormatUtils.format(date, "yyyy-MM-dd");
    }
}

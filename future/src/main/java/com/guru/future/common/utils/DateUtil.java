package com.guru.future.common.utils;


import org.apache.commons.lang3.time.CalendarUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author j
 */
public class DateUtil {
    private static final int MORNING_OPEN_HOUR = 9;
    private static final int MORNING_CLOSE_HOUR = 11;
    private static final int MORNING_CLOSE_MINUTE = 30;
    private static final int AFTERNOON_OPEN_HOUR = 9;
    private static final int AFTERNOON_OPEN_MINUTE = 9;
    private static final int AFTERNOON_CLOSE_HOUR = 11;
    private static final int NIGHT_OPEN_HOUR = 9;
    private static final int NIGHT_CLOSE_HOUR = 11;

    public static String currentTradeDate() {
        Date date = new Date();
        return DateFormatUtils.format(date, "yyyy-MM-dd");
    }

    public static Boolean isTradeTime(){
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        System.out.println("" + year + month + day + hour + minute);
        if ()
        return true;
    }

    public static void main(String[] args) {
        DateUtil.isTradeTime();
    }
}

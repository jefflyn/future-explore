package com.guru.future.common.utils;


import org.apache.commons.lang3.time.CalendarUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author j
 */
public class DateUtil {
    private static final int MORNING_OPEN_HOUR = 9;
    private static final int MORNING_CLOSE_HOUR = 11;
    private static final int MORNING_CLOSE_MINUTE = 30;
    private static final int AFTERNOON_OPEN_HOUR = 13;
    private static final int AFTERNOON_OPEN_MINUTE = 30;
    private static final int AFTERNOON_CLOSE_HOUR = 15;
    private static final int NIGHT_OPEN_HOUR = 21;
    private static final int NIGHT_CLOSE_HOUR = 3;

    public static String currentTradeDate() {
        Date date = new Date();
        return DateFormatUtils.format(date, "yyyy-MM-dd");
    }

    public static Boolean isTradeTime() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);

        Calendar morningOpen = Calendar.getInstance();
        morningOpen.set(year, month, day, MORNING_OPEN_HOUR, 0, 0);
        Calendar morningClose = Calendar.getInstance();
        morningClose.set(year, month, day, MORNING_CLOSE_HOUR, MORNING_CLOSE_MINUTE, 0);

        Calendar afternoonOpen = Calendar.getInstance();
        afternoonOpen.set(year, month, day, AFTERNOON_OPEN_HOUR, AFTERNOON_OPEN_MINUTE, 0);
        Calendar afternoonClose = Calendar.getInstance();
        afternoonClose.set(year, month, day, AFTERNOON_CLOSE_HOUR, 0, 0);

        Calendar nightOpen = Calendar.getInstance();
        nightOpen.set(year, month, day, NIGHT_OPEN_HOUR, 0, 0);
        Calendar nightClose = Calendar.getInstance();
        nightClose.set(year, month, day, NIGHT_CLOSE_HOUR, 59, 59);

        if ((now.after(morningOpen) && now.before(morningClose))
                || (now.after(afternoonOpen) && now.before(afternoonClose))
                || (now.after(nightOpen) && now.before(nightClose))
                || hour <= NIGHT_CLOSE_HOUR) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(DateUtil.isTradeTime());
    }
}

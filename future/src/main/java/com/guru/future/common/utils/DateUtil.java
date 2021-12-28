package com.guru.future.common.utils;


import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author j
 */
@Slf4j
public class DateUtil {
    private static final boolean TRADE_TIME_TEMP = false;
    private static final long SECOND_SCALE = 1000L;
    private static final long MINUTE_SCALE = 60L * SECOND_SCALE;
    private static final long HOUR_SCALE = 60L * MINUTE_SCALE;
    private static final long DAY_SCALE = 24L * HOUR_SCALE;

    protected static final Map<Integer, List<Integer>> HOLIDAY_MAP = new HashMap<>();

    static {
        HOLIDAY_MAP.put(Calendar.JANUARY, Lists.newArrayList(3, 31));
        HOLIDAY_MAP.put(Calendar.FEBRUARY, Lists.newArrayList(1, 2, 3, 4));
        HOLIDAY_MAP.put(Calendar.APRIL, Lists.newArrayList(4, 5));
        HOLIDAY_MAP.put(Calendar.MAY, Lists.newArrayList(2, 3, 4));
        HOLIDAY_MAP.put(Calendar.JUNE, Lists.newArrayList(3));
        HOLIDAY_MAP.put(Calendar.SEPTEMBER, Lists.newArrayList(12));
        HOLIDAY_MAP.put(Calendar.OCTOBER, Lists.newArrayList(3, 4, 5, 6, 7));
    }

    /**
     * private static final int MORNING_OPEN_HOUR = 8;
     * private static final int MORNING_OPEN_MINUTE = 59;
     * private static final int MORNING_CLOSE_HOUR = 11;
     * private static final int MORNING_CLOSE_MINUTE = 30;
     * private static final int AFTERNOON_OPEN_HOUR = 13;
     * private static final int AFTERNOON_OPEN_MINUTE = 29;
     * private static final int AFTERNOON_CLOSE_HOUR = 15;
     * private static final int NIGHT_OPEN_HOUR = 20;
     * private static final int NIGHT_OPEN_MINUTE = 59;
     * private static final int NIGHT_CLOSE_HOUR = 23;
     * private static final int MID_NIGHT_CLOSE_HOUR = 3;
     **/

    public static final String AFTERNOON_CLOSE_TIME = "15:00:00";

    public static final String TRADE_DATE_PATTERN = "yyyy-MM-dd";
    public static final String HOUR_MINUTE_PATTERN = "HH:mm";
    public static final String COMMON_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_PATTERN = "HH:mm:ss";

    private DateUtil() {
    }

    public static String toHourMinute(Date time) {
        return DateFormatUtils.format(time, HOUR_MINUTE_PATTERN);
    }

    public static Boolean isHoliday(Date date) {
        return isHoliday(DateUtils.toCalendar(date));
    }

    public static Boolean isHoliday(Calendar date) {
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);
        int weekDay = date.get(Calendar.DAY_OF_WEEK);
        boolean isWeekend = Lists.newArrayList(Calendar.SATURDAY, Calendar.SUNDAY).contains(weekDay);
        boolean isHoliday = HOLIDAY_MAP.get(month) != null && HOLIDAY_MAP.get(month).contains(day);
        return isWeekend || isHoliday;
    }

    public static String currentTradeDate() {
        Date date = new Date();
        if (Boolean.TRUE.equals(isHoliday(date))) {
            return getNextTradeDate(date);
        }
        return DateFormatUtils.format(date, TRADE_DATE_PATTERN);
    }

    public static String latestTradeDate() {
        Date date = new Date();
        if (Boolean.TRUE.equals(isHoliday(date))) {
            return getLastTradeDate(date);
        }
        return DateFormatUtils.format(date, TRADE_DATE_PATTERN);
    }

    public static String currentTime() {
        Date date = new Date();
        return DateFormatUtils.format(date, TIME_PATTERN);
    }

    public static String getLastTradeDate(String currentDate) {
        if (Strings.isNullOrEmpty(currentDate)) {
            return getLastTradeDate(new Date());
        }
        try {
            return getLastTradeDate(DateUtils.parseDate(currentDate, TRADE_DATE_PATTERN));
        } catch (Exception e) {
            log.error("parse last trade date failed, error={}", e);
            return currentDate;
        }
    }

    public static String getLastTradeDate(Date currentDate) {
        if (currentDate == null) {
            currentDate = new Date();
        }
        Calendar calendar = DateUtils.toCalendar(currentDate);
        for (int addDays = 0; addDays > -9; addDays--) {
            Date preDate = DateUtils.addDays(calendar.getTime(), addDays - 1);
            if (Boolean.FALSE.equals(isHoliday(preDate))) {
                return DateFormatUtils.format(preDate, TRADE_DATE_PATTERN);
            }
        }
        return DateFormatUtils.format(currentDate, TRADE_DATE_PATTERN);
    }

    public static String getNextTradeDate(String currentDate) {
        if (Strings.isNullOrEmpty(currentDate)) {
            return getNextTradeDate(new Date());
        }
        try {
            return getNextTradeDate(DateUtils.parseDate(currentDate, TRADE_DATE_PATTERN));
        } catch (Exception e) {
            log.error("parse next trade date failed, error={}", e);
            return currentDate;
        }
    }

    public static String getNextTradeDate(Date currentDate) {
        if (currentDate == null) {
            currentDate = new Date();
        }
        Calendar calendar = DateUtils.toCalendar(currentDate);
        for (int addDays = 0; addDays < 9; addDays++) {
            Date nextDate = DateUtils.addDays(calendar.getTime(), addDays + 1);
            if (Boolean.FALSE.equals(isHoliday(nextDate))) {
                return DateFormatUtils.format(nextDate, TRADE_DATE_PATTERN);
            }
        }
        return DateFormatUtils.format(currentDate, TRADE_DATE_PATTERN);
    }

    /**
     * [20:59-24:00)
     * [0:00 - 08:59] x
     **/
    public static Boolean isNight() {
        Date date = new Date();
        if (Boolean.TRUE.equals(isHoliday(date))) {
            return true;
        }
        String currentTime = DateFormatUtils.format(date, HOUR_MINUTE_PATTERN);
        return currentTime.compareTo("20:59") >= 0 && currentTime.compareTo("24:00") < 0;
    }

    public static Boolean dayClose() {
        Date date = new Date();
        if (Boolean.TRUE.equals(isHoliday(date))) {
            return false;
        }
        String currentTime = DateFormatUtils.format(date, HOUR_MINUTE_PATTERN);
        return currentTime.compareTo("15:00") > 0;
    }

    public static boolean beforeBidTime() {
        Date now = new Date();
        // holiday
        if (Boolean.TRUE.equals(isHoliday(now))) {
            return false;
        }
        String nowTime = DateFormatUtils.format(now, TIME_PATTERN);

        String morningOpen = "09:00:00";
        String nightOpen = "21:00:00";

        return nowTime.compareTo(morningOpen) < 0
                || (nowTime.compareTo(AFTERNOON_CLOSE_TIME) > 0 && nowTime.compareTo(nightOpen) < 0);
    }

    public static Boolean isTradeTime() {
        Date now = new Date();
        // holiday
        if (Boolean.TRUE.equals(isHoliday(now))) {
            return false;
        }
        String nowTime = DateFormatUtils.format(now, TIME_PATTERN);

        String morningOpen = "09:00:00";
        String morningClose = "11:30:00";

        String afternoonOpen = "13:30:00";

        String nightOpen = "21:00:00";
        String nightClose = "23:00:00";

        if (TRADE_TIME_TEMP) {
            return true;
        }
        return (nowTime.compareTo(morningOpen) >= 0 && nowTime.compareTo(morningClose) <= 0)
                || (nowTime.compareTo(afternoonOpen) >= 0 && nowTime.compareTo(AFTERNOON_CLOSE_TIME) <= 0)
                || (nowTime.compareTo(nightOpen) >= 0 && nowTime.compareTo(nightClose) <= 0);
    }

    public static Boolean isPriceMonitorTime() {
        Date now = new Date();
        String nowTime = DateFormatUtils.format(now, TIME_PATTERN);
        String dayMonitorFrom = "09:05:00";
        String nightFrom = "21:05:00";
        return (nowTime.compareTo(dayMonitorFrom) > 0 && nowTime.compareTo(AFTERNOON_CLOSE_TIME) < 0)
                || nowTime.compareTo(nightFrom) > 0;
    }

    public static long diff(Date before, Date after, TimeUnit unit) {
        long diff = after.getTime() - before.getTime();
        if (TimeUnit.DAYS.equals(unit)) {
            return diff / DAY_SCALE;
        } else if (TimeUnit.HOURS.equals(unit)) {
            return diff / HOUR_SCALE;
        } else if (TimeUnit.MINUTES.equals(unit)) {
            return diff / MINUTE_SCALE;
        } else {
            return diff / SECOND_SCALE;
        }
    }

}

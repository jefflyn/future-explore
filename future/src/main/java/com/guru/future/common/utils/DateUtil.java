package com.guru.future.common.utils;


import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.IOException;
import java.text.ParseException;
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
    private static final long SECOND_SCALE = 1000L;
    private static final long MINUTE_SCALE = 60L * SECOND_SCALE;
    private static final long HOUR_SCALE = 60L * MINUTE_SCALE;
    private static final long DAY_SCALE = 24L * HOUR_SCALE;

    public static final Map<Integer, List<Integer>> HOLIDAY_MAP = new HashMap<>();

    static {
        HOLIDAY_MAP.put(Calendar.JANUARY, Lists.newArrayList(1));
        HOLIDAY_MAP.put(Calendar.SEPTEMBER, Lists.newArrayList(19, 20, 21));
        HOLIDAY_MAP.put(Calendar.OCTOBER, Lists.newArrayList(1, 2, 3, 4, 5, 6, 7));
    }

    private static final int MORNING_OPEN_HOUR = 8;
    private static final int MORNING_OPEN_MINUTE = 59;
    private static final int MORNING_CLOSE_HOUR = 11;
    private static final int MORNING_CLOSE_MINUTE = 30;
    private static final int AFTERNOON_OPEN_HOUR = 13;
    private static final int AFTERNOON_OPEN_MINUTE = 29;
    private static final int AFTERNOON_CLOSE_HOUR = 15;
    private static final int NIGHT_OPEN_HOUR = 20;
    private static final int NIGHT_OPEN_MINUTE = 59;
    private static final int NIGHT_CLOSE_HOUR = 3;

    public static final String TRADE_DATE_PATTERN = "yyyy-MM-dd";
    public static final String HOUR_MINUTE_PATTERN = "HH:mm";
    public static final String COMMON_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_PATTERN = "HH:mm:ss";


    public static Boolean isHoliday(Date date) {
        return isHoliday(DateUtils.toCalendar(date));
    }

    public static Boolean isHoliday(Calendar date) {
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);
        int weekDay = date.get(Calendar.DAY_OF_WEEK);
        boolean isWeekend = Lists.newArrayList(Calendar.SATURDAY, Calendar.SUNDAY).contains(weekDay);
        boolean isHoliday = HOLIDAY_MAP.get(month) != null && HOLIDAY_MAP.get(month).contains(day);
        if (isWeekend || isHoliday) {
            return true;
        }
        return false;
    }

    public static String currentTradeDate() {
        Date date = new Date();
        if (isHoliday(date)) {
            return getNextTradeDate(date);
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
            if (!isHoliday(preDate)) {
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
            if (!isHoliday(nextDate)) {
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
        if (isHoliday(date)) {
            return true;
        }
        String currentTime = DateFormatUtils.format(date, HOUR_MINUTE_PATTERN);
        boolean currentNight = currentTime.compareTo("20:59") >= 0 && currentTime.compareTo("24:00") < 0;
//        boolean nextMorning = currentTime.compareTo("00:00") >= 0 && currentTime.compareTo("08:59") < 0;
        return currentNight;
//        return (currentNight || nextMorning);
    }

    public static Boolean dayClose() {
        Date date = new Date();
        if (isHoliday(date)) {
            return false;
        }
        String currentTime = DateFormatUtils.format(date, HOUR_MINUTE_PATTERN);
        boolean dayClose = currentTime.compareTo("15:00") > 0;
        return dayClose;
    }

    public static Boolean isTradeTime() {
        Calendar now = Calendar.getInstance();
        // holiday
        if (isHoliday(now)) {
            return false;
        }
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);

        Calendar morningOpen = Calendar.getInstance();
        morningOpen.set(year, month, day, MORNING_OPEN_HOUR, MORNING_OPEN_MINUTE, 0);
        Calendar morningClose = Calendar.getInstance();
        morningClose.set(year, month, day, MORNING_CLOSE_HOUR, MORNING_CLOSE_MINUTE, 10);

        Calendar afternoonOpen = Calendar.getInstance();
        afternoonOpen.set(year, month, day, AFTERNOON_OPEN_HOUR, AFTERNOON_OPEN_MINUTE, 0);
        Calendar afternoonClose = Calendar.getInstance();
        afternoonClose.set(year, month, day, AFTERNOON_CLOSE_HOUR, 0, 10);

        Calendar nightOpen = Calendar.getInstance();
        nightOpen.set(year, month, day, NIGHT_OPEN_HOUR, NIGHT_OPEN_MINUTE, 0);
        Calendar nightClose = Calendar.getInstance();
        nightClose.set(year, month, day, NIGHT_CLOSE_HOUR, 59, 59);

        boolean isTradeTime = (now.after(morningOpen) && now.before(morningClose))
                || (now.after(afternoonOpen) && now.before(afternoonClose))
                || (now.after(nightOpen) && now.before(nightClose))
                || hour <= NIGHT_CLOSE_HOUR;

        return isTradeTime;
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

    public static void main(String[] args) throws Exception {
        System.out.println(diff(DateUtils.parseDate("2021-08-23 12:41:00", COMMON_DATE_PATTERN),
                DateUtils.parseDate("2021-08-23 12:41:10", COMMON_DATE_PATTERN), null));
        System.out.println(diff(DateUtils.parseDate("2021-08-23 12:41:00", COMMON_DATE_PATTERN),
                DateUtils.parseDate("2021-08-23 12:43:11", COMMON_DATE_PATTERN), TimeUnit.MINUTES));
        System.out.println(diff(DateUtils.parseDate("2021-08-23 12:41:00", COMMON_DATE_PATTERN),
                DateUtils.parseDate("2021-08-23 13:41:59", COMMON_DATE_PATTERN), TimeUnit.HOURS));
        System.out.println(diff(DateUtils.parseDate("2021-08-23 12:41:40", COMMON_DATE_PATTERN),
                DateUtils.parseDate("2021-08-25 12:39:10", COMMON_DATE_PATTERN), TimeUnit.DAYS));
//        System.out.println(DateUtil.isTradeTime());
//        System.out.println(DateUtil.getLastTradeDate(new Date()));
//        System.out.println(DateUtil.getNextTradeDate(new Date()));

    }
}

package com.guru.future.common.utils;


import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author j
 */
public class DateUtil {
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
    private static final int AFTERNOON_OPEN_MINUTE = 30;
    private static final int AFTERNOON_CLOSE_HOUR = 15;
    private static final int NIGHT_OPEN_HOUR = 20;
    private static final int NIGHT_OPEN_MINUTE = 59;
    private static final int NIGHT_CLOSE_HOUR = 3;

    public static final String TRADE_DATE_PATTERN = "yyyy-MM-dd";

    public static String currentTradeDate() {
        Date date = new Date();
        return DateFormatUtils.format(date, TRADE_DATE_PATTERN);
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
        if (isWeekend || isHoliday) {
            return true;
        }
        return false;
    }

    public static String getLastTradeDate(String currentDate) throws ParseException {
        if (Strings.isNullOrEmpty(currentDate)) {
            return getLastTradeDate(new Date());
        }
        return getLastTradeDate(DateUtils.parseDate(currentDate, TRADE_DATE_PATTERN));
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

    public static String getNextTradeDate(String currentDate) throws ParseException {
        if (Strings.isNullOrEmpty(currentDate)) {
            return getNextTradeDate(new Date());
        }
        return getNextTradeDate(DateUtils.parseDate(currentDate, TRADE_DATE_PATTERN));

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
        morningClose.set(year, month, day, MORNING_CLOSE_HOUR, MORNING_CLOSE_MINUTE, 0);

        Calendar afternoonOpen = Calendar.getInstance();
        afternoonOpen.set(year, month, day, AFTERNOON_OPEN_HOUR, AFTERNOON_OPEN_MINUTE, 0);
        Calendar afternoonClose = Calendar.getInstance();
        afternoonClose.set(year, month, day, AFTERNOON_CLOSE_HOUR, 0, 0);

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

    public static void main(String[] args) {
        System.out.println(DateUtil.isTradeTime());
        System.out.println(DateUtil.getLastTradeDate(new Date()));
        System.out.println(DateUtil.getNextTradeDate(new Date()));

    }
}

package com.guru.future.common.utils;


import org.springframework.util.NumberUtils;

import java.math.BigDecimal;

public class NumberUtil {
    public static String decimal2String(BigDecimal numberVal) {
        String numberStr = String.valueOf(numberVal);
        System.out.println(numberStr.contains("."));
        String[] numberArr = numberStr.split("\\.");
        if (numberArr.length > 1) {
            String digit = numberArr[1];
            int length = digit.length() > 2 ? 2 : digit.length();
            if (Integer.valueOf(digit.substring(0, length)) > 0) {
                return numberStr;
            }
        }
        return numberArr[0];
    }

    public static BigDecimal string2Decimal(String numberVal) {
        if (numberVal == null) {
            return BigDecimal.ZERO;
        }
        return NumberUtils.convertNumberToTargetClass(Float.valueOf(numberVal), BigDecimal.class);
    }

    public static Integer string2Integer(String numberVal) {
        if (numberVal == null) {
            return 0;
        }
        return NumberUtils.convertNumberToTargetClass(Float.valueOf(numberVal), Integer.class);
    }
}

package com.guru.future.common.utils;


import org.springframework.util.NumberUtils;

import java.math.BigDecimal;

public class NumberUtil {
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

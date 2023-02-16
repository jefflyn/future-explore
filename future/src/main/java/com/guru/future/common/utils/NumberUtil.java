package com.guru.future.common.utils;


import org.springframework.util.NumberUtils;

import java.math.BigDecimal;

import static java.lang.Math.abs;

public class NumberUtil {

    private NumberUtil() {
    }

    public static Integer toInteger(Object object) {
        if (object == null) {
            return Integer.valueOf(0);
        }
        return (Integer) object;
    }

    public static String changePrefix(BigDecimal change) {
        if (BigDecimal.ZERO.compareTo(change) < 0) {
            return "+" + change;
        }
        return change.toString();
    }

    public static float changeDiff(float a, float b) {
        return abs(a - b) * 100 / b;
    }

    /**
     * 去掉无效的小数位0
     *
     * @param numberVal
     * @return
     */
    public static String price2String(BigDecimal numberVal) {
        String numberStr = String.valueOf(numberVal);
        String[] numberArr = numberStr.split("\\.");
        if (numberArr.length > 1) {
            String digit = numberArr[1];
            int length = digit.length() > 2 ? 2 : digit.length();
            if (length == 2) {
                digit = digit.substring(0, 2);
            }
            if (Integer.valueOf(digit) == 0) {
                return numberArr[0];
            }
            String newDigitStr = digit.substring(0, length);
            if (Integer.valueOf(newDigitStr) > 0
                    && newDigitStr.lastIndexOf("0") == length - 1) {
                return numberArr[0] + "." + newDigitStr.substring(0, 1);
            }
            return numberArr[0] + "." + newDigitStr;
        }
        return numberArr[0];
    }

    public static BigDecimal toFuturePrice(BigDecimal numberVal) {
        String priceStr = price2String(numberVal);
        return new BigDecimal(priceStr);
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

    public static void main(String[] args) {
        BigDecimal price = new BigDecimal(123.202D);
        System.out.println(toFuturePrice(price));
    }
}

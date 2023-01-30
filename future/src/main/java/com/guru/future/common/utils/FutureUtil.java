package com.guru.future.common.utils;


import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author j
 */
@Slf4j
public class FutureUtil {
    public static final String PERCENTAGE_SYMBOL = "%";

    private FutureUtil() {
    }

    public static String code2Symbol(String code) {
        if (Strings.isNotBlank(code) && code.length() > 1) {
            if (NumberUtil.isNumber(code.substring(1, 2))) {
                return code.substring(0, 1);
            } else {
                return code.substring(0, 2);
            }
        }
        return code;
    }

    public static String getDirectionTag(BigDecimal rate) {
        if (BigDecimal.ZERO.compareTo(rate) < 0) {
            return "/";
        }
        return "\\";
    }

    public static BigDecimal calcChange(BigDecimal price, BigDecimal prePrice) {
        BigDecimal change = (price.subtract(prePrice)
                .multiply(BigDecimal.valueOf(100))
                .divide(prePrice, 2, RoundingMode.HALF_UP));
        return change;
    }

    /**
     * (p-l) / (h-l)
     *
     * @param isUp
     * @param price
     * @param high
     * @param low
     * @return
     */
    public static int calcPosition(Boolean isUp, BigDecimal price, BigDecimal high, BigDecimal low) {
        BigDecimal position = BigDecimal.ZERO;
        if (high.compareTo(low) != 0) {
            position = (price.subtract(low)).multiply(BigDecimal.valueOf(100))
                    .divide((high.subtract(low)), 0, RoundingMode.HALF_UP);
            if (position.intValue() < 0) {
                position = BigDecimal.ZERO;
            } else if (position.intValue() > 100) {
                position = BigDecimal.valueOf(100);
            }
        } else if (isUp && high.compareTo(low) == 0 && price.compareTo(BigDecimal.ZERO) > 0) {
            position = BigDecimal.valueOf(100);
        }
        return position.intValue();
    }

    public static String generateWave(BigDecimal a, BigDecimal b, BigDecimal c, BigDecimal d, BigDecimal price) {
        List<String> waveList = new ArrayList<>();
//        StringBuilder waveStr = new StringBuilder();
        if (a == null || price == null) {
            return Strings.EMPTY;
        }

        BigDecimal waveA;
        if (b != null) {
            waveA = b.subtract(a).multiply(BigDecimal.valueOf(100)).divide(a, 2, RoundingMode.HALF_UP);
//            waveStr.append(getDirectionTag(waveA)).append(waveA).append(PERCENTAGE_SYMBOL);
            waveList.add(waveA + PERCENTAGE_SYMBOL);
        } else {
            waveA = price.subtract(a).multiply(BigDecimal.valueOf(100)).divide(price, 2, RoundingMode.HALF_UP);
//            waveStr.append(getDirectionTag(waveA)).append(waveA).append(PERCENTAGE_SYMBOL);
            waveList.add(waveA + PERCENTAGE_SYMBOL);
            return waveList.toString();
        }
        BigDecimal waveB;
        if (c != null) {
            waveB = c.subtract(b).multiply(BigDecimal.valueOf(100)).divide(b, 2, RoundingMode.HALF_UP);
//            waveStr.append(getDirectionTag(waveB)).append(waveB).append(PERCENTAGE_SYMBOL);
            waveList.add(waveB + PERCENTAGE_SYMBOL);
        } else {
            waveB = price.subtract(b).multiply(BigDecimal.valueOf(100)).divide(b, 2, RoundingMode.HALF_UP);
//            waveStr.append(getDirectionTag(waveB)).append(waveB).append(PERCENTAGE_SYMBOL);
            waveList.add(waveB + PERCENTAGE_SYMBOL);
            return waveList.toString();
        }
        BigDecimal waveC;
        if (d != null) {
            waveC = d.subtract(c).multiply(BigDecimal.valueOf(100)).divide(c, 2, RoundingMode.HALF_UP);
            BigDecimal waveD = price.subtract(d).multiply(BigDecimal.valueOf(100)).divide(d, 2, RoundingMode.HALF_UP);
            waveList.add(waveC + PERCENTAGE_SYMBOL);
            waveList.add(waveD + PERCENTAGE_SYMBOL);
        } else {
            waveC = price.subtract(c).multiply(BigDecimal.valueOf(100)).divide(c, 2, RoundingMode.HALF_UP);
            waveList.add(waveC + PERCENTAGE_SYMBOL);
        }
//        waveStr.append(getDirectionTag(waveC)).append(waveC).append(PERCENTAGE_SYMBOL);
        if (waveList.size() > 3) {
            return waveList.subList(waveList.size() - 3, waveList.size()).toString();
        }
        return waveList.toString();
    }

    public static String generateWave(BigDecimal a, BigDecimal b, BigDecimal c, BigDecimal price) {
        StringBuilder waveStr = new StringBuilder();
        if (a == null || price == null) {
            return Strings.EMPTY;
        }
        BigDecimal waveA;
        if (b != null) {
            waveA = b.subtract(a).multiply(BigDecimal.valueOf(100)).divide(a, 2, RoundingMode.HALF_UP);
            waveStr.append(getDirectionTag(waveA)).append(waveA).append(PERCENTAGE_SYMBOL);
        } else {
            waveA = price.subtract(a).multiply(BigDecimal.valueOf(100)).divide(price, 2, RoundingMode.HALF_UP);
            waveStr.append(getDirectionTag(waveA)).append(waveA).append(PERCENTAGE_SYMBOL);
            return waveStr.toString();
        }
        BigDecimal waveB;
        if (c != null) {
            waveB = c.subtract(b).multiply(BigDecimal.valueOf(100)).divide(b, 2, RoundingMode.HALF_UP);
            boolean waveEnd = false;
            if (waveB.compareTo(BigDecimal.ZERO) > 0) {
                if (price.compareTo(c) > 0) {
                    waveB = price.subtract(b).multiply(BigDecimal.valueOf(100)).divide(b, 2, RoundingMode.HALF_UP);
                    waveEnd = true;
                }
            } else {
                if (price.compareTo(c) < 0) {
                    waveB = price.subtract(b).multiply(BigDecimal.valueOf(100)).divide(b, 2, RoundingMode.HALF_UP);
                    waveEnd = true;
                }
            }
            String tagB = getDirectionTag(waveB);
            waveStr.append(tagB).append(waveB).append(PERCENTAGE_SYMBOL);
            if (Boolean.FALSE.equals(waveEnd)) {
                BigDecimal waveC = price.subtract(c).multiply(BigDecimal.valueOf(100)).divide(c, 2, RoundingMode.HALF_UP);
                String tagC = getDirectionTag(waveC);
                waveStr.append(tagC).append(waveC).append(PERCENTAGE_SYMBOL);
            }
        } else {
            waveB = price.subtract(b).multiply(BigDecimal.valueOf(100)).divide(b, 2, RoundingMode.HALF_UP);
            waveStr.append(getDirectionTag(waveB)).append(waveB).append(PERCENTAGE_SYMBOL);
        }
        return waveStr.toString();
    }

    public static void main(String[] args) {
        System.out.println(code2Symbol("SA10"));
    }

}

package com.guru.future.common.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author j
 */
@Slf4j
public class WaveUtil {
    public static final String PERCENTAGE_SYMBOL = "%";

    private WaveUtil() {
    }

    public static String getDirectionTag(BigDecimal rate) {
        if (BigDecimal.ZERO.compareTo(rate) < 0) {
            return "/";
        }
        return "\\";
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
        BigDecimal waveB = null;
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
                BigDecimal waveC = price.subtract(c).multiply(BigDecimal.valueOf(100)).divide(price, 2, RoundingMode.HALF_UP);
                String tagC = getDirectionTag(waveC);
                waveStr.append(tagC).append(waveC).append(PERCENTAGE_SYMBOL);
            }
        } else {
            waveB = price.subtract(b).multiply(BigDecimal.valueOf(100)).divide(price, 2, RoundingMode.HALF_UP);
            waveStr.append(getDirectionTag(waveB)).append(waveB).append(PERCENTAGE_SYMBOL);
        }
        return waveStr.toString();
    }

}

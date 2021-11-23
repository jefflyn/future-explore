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
        BigDecimal waveA = null;
        if (b != null) {
            waveA = b.subtract(a).multiply(BigDecimal.valueOf(100)).divide(a, 2, RoundingMode.HALF_UP);
            waveStr.append(waveA).append(PERCENTAGE_SYMBOL).append(getDirectionTag(waveA));
        } else {
            waveA = price.subtract(a).multiply(BigDecimal.valueOf(100)).divide(price, 2, RoundingMode.HALF_UP);
            waveStr.append(waveA).append(PERCENTAGE_SYMBOL).append(getDirectionTag(waveA));
            return waveStr.toString();
        }
        BigDecimal waveB = null;
        if (c != null) {
            waveB = c.subtract(b).multiply(BigDecimal.valueOf(100)).divide(b, 2, RoundingMode.HALF_UP);
            BigDecimal waveC = price.subtract(c).multiply(BigDecimal.valueOf(100)).divide(price, 2, RoundingMode.HALF_UP);
            String tagB = getDirectionTag(waveB);
            String tagC = getDirectionTag(waveC);
            waveStr.append(waveB).append(PERCENTAGE_SYMBOL).append(tagB);
            waveStr.append(waveC).append(PERCENTAGE_SYMBOL).append(tagC);
            if (tagB.equals(tagC)) {
                log.warn("wave {} ERROR, please check!", waveStr);
            }
            return waveStr.toString();
        } else {
            waveB = price.subtract(b).multiply(BigDecimal.valueOf(100)).divide(price, 2, RoundingMode.HALF_UP);
            waveStr.append(waveB).append(PERCENTAGE_SYMBOL).append(getDirectionTag(waveB));
            return waveStr.toString();
        }

    }

}

package com.guru.future.domain;

import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FutureLiveDO implements Comparable<FutureLiveDO>{
    private String tradeDate;

    private String type;

    private String code;

    private String name;

    private BigDecimal price;

    private BigDecimal change;

    private BigDecimal bid1;

    private BigDecimal ask1;

    private BigDecimal open;

    private BigDecimal low;

    private BigDecimal high;

    private Integer position;

    private BigDecimal amp;

    private String wave;

    private BigDecimal lowestChange;

    private BigDecimal highestChange;

    private Date updateTime;

    public String changeFlag() {
        return code + "," + ObjectUtils.defaultIfNull(price, 0).floatValue() +
                "," + ObjectUtils.defaultIfNull(bid1, 0).floatValue() +
                "," + ObjectUtils.defaultIfNull(ask1, 0).floatValue() +
                "," + ObjectUtils.defaultIfNull(low, 0).floatValue() +
                "," + ObjectUtils.defaultIfNull(high, 0).floatValue();
    }

    @Override
    public int compareTo(FutureLiveDO liveDO) {
        return this.change.compareTo(liveDO.change);
    }
}
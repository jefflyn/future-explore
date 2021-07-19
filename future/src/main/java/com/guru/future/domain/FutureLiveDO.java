package com.guru.future.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FutureLiveDO {
    private String code;

    private String name;

    private BigDecimal price;

    private BigDecimal change;

    private BigDecimal bid1;

    private BigDecimal ask1;

    private BigDecimal low;

    private BigDecimal high;

    private BigDecimal position;

    private BigDecimal amp;

    private String wave;

    private BigDecimal lowestChange;

    private BigDecimal highestChange;

    private Date updateTime;

}
package com.guru.future.common.entity.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Wave {
    private String code;

    private BigDecimal a;

    private BigDecimal b;

    private BigDecimal c;

    private BigDecimal d;

    private BigDecimal ap;

    private BigDecimal bp;

    private BigDecimal cp;

    private BigDecimal dp;
}
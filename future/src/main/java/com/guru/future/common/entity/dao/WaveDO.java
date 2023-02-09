package com.guru.future.common.entity.dao;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class WaveDO {
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
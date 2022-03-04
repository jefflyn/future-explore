package com.guru.future.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FutureLogDO {
    private Long id;

    private String tradeDate;

    private String code;

    private String name;

    private String type;

    private Integer factor;

    private BigDecimal diff;

    private String content;

    private String option;

    private BigDecimal suggest;

    private BigDecimal pctChange;

    private Integer position;

    private Date logTime;

    private String remark;

    private String temp;
}
package com.guru.future.common.entity.dao;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TsFutureDailyDO {
    private String tsCode;

    private String tradeDate;

    private BigDecimal preClose;

    private BigDecimal preSettle;

    private BigDecimal open;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal close;

    private BigDecimal settle;

    private BigDecimal closeChange;

    private BigDecimal settleChange;

    private BigDecimal dealVol;

    private BigDecimal dealAmount;

    private BigDecimal holdVol;

    private BigDecimal holdChange;

    private Date createTime;

}
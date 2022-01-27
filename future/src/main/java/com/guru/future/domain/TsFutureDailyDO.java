package com.guru.future.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TsFutureDailyDO {
    private String tsCode;

    private String tradeDate;

    private String preClose;

    private BigDecimal preSettle;

    private BigDecimal open;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal close;

    private BigDecimal settle;

    private BigDecimal closeChange;

    private BigDecimal settleChange;

    private Integer dealVol;

    private BigDecimal dealAmount;

    private Integer holdVol;

    private Integer holdChange;

    private Date createTime;

}
package com.guru.future.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OpenGapDO {
    private String tradeDate;

    private String code;

    private String name;

    private String category;

    private BigDecimal preClose;

    private BigDecimal open;

    private BigDecimal gapRate;

    private Boolean dayGap;

    private String remark;

    private BigDecimal buyLow;

    private BigDecimal sellHigh;

}
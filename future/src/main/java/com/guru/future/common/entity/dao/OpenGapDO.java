package com.guru.future.common.entity.dao;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OpenGapDO {
    private String tradeDate;

    private String code;

    private String name;

    private String category;

    private BigDecimal preClose;

    private BigDecimal preSettle;

    private BigDecimal preHigh;

    private BigDecimal preLow;

    private BigDecimal open;

    private BigDecimal openChange;

    private BigDecimal gapRate;

    private Boolean dayGap;

    private String remark;

    private Integer contractPosition;

    private BigDecimal buyLow;

    private BigDecimal sellHigh;

    private Integer suggest;

    private BigDecimal suggestPrice;
}
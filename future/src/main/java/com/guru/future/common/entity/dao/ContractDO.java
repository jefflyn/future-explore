package com.guru.future.common.entity.dao;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ContractDO {
    private String code;

    private String symbol;

    private String tsCode;

    private Integer main;

    private BigDecimal low;

    private BigDecimal high;

    private Date lowTime;

    private Date highTime;

    private Integer selected;

    private Date createTime;

    private Date updateTime;

    private Integer deleted;
}
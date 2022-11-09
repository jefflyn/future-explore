package com.guru.future.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ContractDO {
    private String code;

    private String symbol;

    private String tsCode;

    private Integer dominant;

    private BigDecimal low;

    private BigDecimal high;

    private Date lowTime;

    private Date highTime;

    private Integer selected;

    private String remark;

    private Date createTime;

    private Date updateTime;

}
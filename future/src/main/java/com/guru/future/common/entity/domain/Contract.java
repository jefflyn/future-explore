package com.guru.future.common.entity.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Contract {
    private String code;

    private String symbol;

    private String tsCode;

    private Integer main;

    private BigDecimal low;

    private BigDecimal high;

    private String lowTime;

    private String highTime;

    private Integer selected;

    private Date createTime;

    private Date updateTime;

    private Integer deleted;

    public boolean isMain() {
        return this.main == 1;
    }
}
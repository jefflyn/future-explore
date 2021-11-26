package com.guru.future.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FutureBasicDO {
    private String symbol;

    private String name;

    private String type;

    private String code;

    private BigDecimal low;

    private BigDecimal high;

    private BigDecimal a;
    private BigDecimal b;
    private BigDecimal c;

    private Integer amount;

    private String unit;

    private Integer night;

    private String exchange;

    private Integer deleted;

    private Date updateTime;

    private Integer isTarget;

    private String remark;

    public boolean hasNightTrade(){
        return night == 1;
    }
}
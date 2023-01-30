package com.guru.future.common.entity.dao;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FutureCollectDO {
    private Long id;

    private String tradeDate;

    private String code;

    private String name;

    private Integer type;

    private Integer position;

    private BigDecimal price;

    private BigDecimal high;

    private BigDecimal low;

    private Integer dealVol;

    private Integer holdVol;

    private Date createTime;

    private String remark;
}
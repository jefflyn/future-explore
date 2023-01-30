package com.guru.future.common.entity.dao;

import lombok.Data;

import java.util.Date;

@Data
public class FutureBasicDO {
    private String symbol;

    private String name;

    private String type;

    private Integer profit;

    private Integer night;

    private String exchange;

    private Integer amount;

    private String unit;

    private String relative;

    private String remark;

    private Byte deleted;

    private Date updateTime;

}
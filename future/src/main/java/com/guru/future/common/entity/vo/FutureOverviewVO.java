package com.guru.future.common.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FutureOverviewVO {
    private BigDecimal avg;

    private String name;

    private BigDecimal price;

    private BigDecimal change;

    private BigDecimal low;

    private BigDecimal high;

    private Boolean highTop;

    private BigDecimal position;

    private String direction = " ";

    private String wave;

}
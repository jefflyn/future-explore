package com.guru.future.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FutureLogDO {
    private Long id;

    private String name;

    private String type;

    private String content;

    private BigDecimal price;

    private BigDecimal pctChange;

    private Integer position;

    private Date logTime;

    private String remark;
}
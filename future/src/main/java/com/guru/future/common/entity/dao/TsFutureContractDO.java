package com.guru.future.common.entity.dao;

import lombok.Data;

@Data
public class TsFutureContractDO {
    private String tsCode;

    private String symbol;

    private String exchange;

    private String name;

    private String futCode;

    private Integer type;
}
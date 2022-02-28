package com.guru.future.domain;

import lombok.Data;

@Data
public class TsFutureHoldingDO {
    private String tradeDate;

    private String symbol;

    private String broker;

    private Integer vol;

    private Integer volChg;

    private Integer longHld;

    private Integer longChg;

    private Integer shortHld;

    private Integer shortChg;
}
package com.guru.future.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public class FutureDailyDO {
    private Long id;

    private String symbol;

    private String tradeDate;

    private String code;

    private String name;

    private BigDecimal close;

    private BigDecimal closeChange;

    private BigDecimal settle;

    private BigDecimal settleChange;

    private BigDecimal open;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal hlDiff;

    private BigDecimal amplitude;

    private BigDecimal preClose;

    private BigDecimal preSettle;

    private Integer dealVol;

    private Integer holdVol;

    private String exchange;

    private Date createTime;

    private Date updateTime;

    private String remark;

    public FutureDailyDO(String symbol, String tradeDate, String code, String name, BigDecimal preClose) {
        this.symbol = symbol;
        this.tradeDate = tradeDate;
        this.code = code;
        this.name = name;
        this.open = open;
        this.preClose = preClose;
    }

    public String changFlag() {
        return tradeDate + "," + code
                + "," + ObjectUtils.defaultIfNull(close, 0).floatValue()
                + "," + ObjectUtils.defaultIfNull(open, 0).floatValue();
    }
}
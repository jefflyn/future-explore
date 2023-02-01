package com.guru.future.common.entity.dao;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public class TradeDailyDO {
    private Long id;

    private String symbol;

    private String tradeDate;

    private String code;

    private BigDecimal open;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal close;

    private BigDecimal settle;

    private BigDecimal preClose;

    private BigDecimal preSettle;

    private BigDecimal closeChange;

    private BigDecimal settleChange;

    private Integer dealVol;

    private Integer holdVol;

    private Date createTime;

    public TradeDailyDO(String symbol, String tradeDate, String code, BigDecimal preClose) {
        this.symbol = symbol;
        this.tradeDate = tradeDate;
        this.code = code;
        this.preClose = preClose;
    }

    public String changFlag() {
        return tradeDate + "," + code
                + "," + ObjectUtils.defaultIfNull(close, 0).floatValue()
                + "," + ObjectUtils.defaultIfNull(open, 0).floatValue();
    }
}
package com.guru.future.domain;

import java.math.BigDecimal;

public class OpenGapDO {
    private String tradeDate;

    private String code;

    private String name;

    private String category;

    private BigDecimal preClose;

    private BigDecimal open;

    private BigDecimal gapRate;

    private String remark;

    private BigDecimal buyLow;

    private BigDecimal sellHigh;

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate == null ? null : tradeDate.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category == null ? null : category.trim();
    }

    public BigDecimal getPreClose() {
        return preClose;
    }

    public void setPreClose(BigDecimal preClose) {
        this.preClose = preClose;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getGapRate() {
        return gapRate;
    }

    public void setGapRate(BigDecimal gapRate) {
        this.gapRate = gapRate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public BigDecimal getBuyLow() {
        return buyLow;
    }

    public void setBuyLow(BigDecimal buyLow) {
        this.buyLow = buyLow;
    }

    public BigDecimal getSellHigh() {
        return sellHigh;
    }

    public void setSellHigh(BigDecimal sellHigh) {
        this.sellHigh = sellHigh;
    }
}
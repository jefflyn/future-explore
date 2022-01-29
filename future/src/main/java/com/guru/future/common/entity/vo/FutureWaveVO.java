package com.guru.future.common.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FutureWaveVO implements Comparable<FutureWaveVO> {
    private String code;
    private String begin;
    private String end;
    private String status;
    private BigDecimal begin_price;
    private BigDecimal end_price;
    private int days;
    private BigDecimal change;

    @Override
    public int compareTo(FutureWaveVO o) {
        return begin.compareTo(o.begin);
    }
}
package com.guru.future.common.entity.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class Wave {
    private String code;

    private BigDecimal a;

    private BigDecimal b;

    private BigDecimal c;

    private BigDecimal d;

    private BigDecimal ap;

    private BigDecimal bp;

    private BigDecimal cp;

    private BigDecimal dp;

    public BigDecimal getMinP() {
        List<BigDecimal> list = new ArrayList<>();
        if (ap.compareTo(BigDecimal.ZERO) > 0) {
            list.add(ap);
        }
        if (bp.compareTo(BigDecimal.ZERO) > 0) {
            list.add(bp);
        }
        if (cp.compareTo(BigDecimal.ZERO) > 0) {
            list.add(cp);
        }
        if (dp.compareTo(BigDecimal.ZERO) > 0) {
            list.add(dp);
        }
        return Collections.min(list);
    }

    public BigDecimal getMaxP() {
        List<BigDecimal> list = new ArrayList<>();
        if (ap.compareTo(BigDecimal.ZERO) > 0) {
            list.add(ap);
        }
        if (bp.compareTo(BigDecimal.ZERO) > 0) {
            list.add(bp);
        }
        if (cp.compareTo(BigDecimal.ZERO) > 0) {
            list.add(cp);
        }
        if (dp.compareTo(BigDecimal.ZERO) > 0) {
            list.add(dp);
        }
        return Collections.max(list);
    }
}
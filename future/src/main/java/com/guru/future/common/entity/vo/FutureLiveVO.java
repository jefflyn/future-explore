package com.guru.future.common.entity.vo;

import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FutureLiveVO implements Comparable<FutureLiveVO> {
    private Integer sortNo;

    private String name;

    private BigDecimal price;

    private BigDecimal change;

    private BigDecimal low;

    private BigDecimal high;

    private BigDecimal position;

    private String direction;

    private String wave;

    @Override
    public int compareTo(FutureLiveVO o) {
        return this.position.compareTo(o.getPosition());
    }

    @Override
    public String toString() {
        return sortNo + direction + " " + name + " " + price + " " + change
                + "[" + low + "-" + high + " " + position + " " + wave;
    }
}
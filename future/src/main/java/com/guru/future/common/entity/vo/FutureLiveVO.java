package com.guru.future.common.entity.vo;

import com.guru.future.common.utils.WaveUtil;
import lombok.Data;

import java.math.BigDecimal;

import static com.guru.future.common.utils.NumberUtil.decimal2String;

@Data
public class FutureLiveVO implements Comparable<FutureLiveVO> {
    private Integer sortNo;

    private String name;

    private BigDecimal price;

    private BigDecimal change;

    private BigDecimal low;

    private BigDecimal high;

    private BigDecimal position;

    private String direction = " ";

    private String wave;

    @Override
    public int compareTo(FutureLiveVO o) {
        return this.position.compareTo(o.getPosition());
    }

    @Override
    public String toString() {
        return sortNo + direction + " " + name + "【" + decimal2String(low) + "-" + decimal2String(high) + "】"
                + decimal2String(price) + " " + change + WaveUtil.PERCENTAGE_SYMBOL
                + "【" + position + "】" + wave;

    }
}
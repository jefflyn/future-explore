package com.guru.future.common.entity.vo;

import com.guru.future.common.utils.FutureUtil;
import com.guru.future.common.utils.NumberUtil;
import lombok.Data;

import java.math.BigDecimal;

import static com.guru.future.common.utils.NumberUtil.price2String;

@Data
public class FutureLiveVO implements Comparable<FutureLiveVO> {
    private Integer sortNo;

    private String name;

    private BigDecimal price;

    private BigDecimal change;

    private BigDecimal low;

    private BigDecimal high;

    private Boolean highTop;

    private Integer position;

    private String direction = " ";

    private String wave;

    @Override
    public int compareTo(FutureLiveVO o) {
        return this.position.compareTo(o.getPosition());
    }

    @Override
    public String toString() {
        String lowHigh = highTop ? "【" + price2String(low) + "-" + price2String(high) + "】"
                : "【" + price2String(high) + "-" + price2String(low) + "】";
        return sortNo + direction + " " + name + " " + NumberUtil.changePrefix(change) + FutureUtil.PERCENTAGE_SYMBOL
                + lowHigh + price2String(price) + "【" + position + "】" + wave;

    }
}
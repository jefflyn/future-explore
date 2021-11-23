package com.guru.future.common.entity.vo;

import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import com.guru.future.common.utils.WaveUtil;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.NumberUtils;

import java.math.BigDecimal;
import java.util.Date;

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
        return sortNo + direction + "  " + name + "  "  + change + WaveUtil.PERCENTAGE_SYMBOL
                + "【" + low + "-" + high + "】" + decimal2String(price) + "  " + position + "  " + wave;
    }
}
package com.guru.future.common.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ContractOpenGapDTO implements Comparable<ContractOpenGapDTO> {
    private String code;

    private String name;

    private String category;

    private BigDecimal preClose;

    private BigDecimal preSettle;

    private BigDecimal preHigh;

    private BigDecimal preLow;

    private BigDecimal open;

    private BigDecimal openChange;

    private BigDecimal gapRate;

    private Boolean dayGap;

    private String remark;

    private Integer contractPosition;

    private String suggest;

    private String tradeDate;

    private BigDecimal buyLow;

    private BigDecimal sellHigh;

    @Override
    public int compareTo(ContractOpenGapDTO contractOpenGapDTO) {
        return Float.valueOf(Math.abs(this.openChange.floatValue()))
                .compareTo(Float.valueOf(Math.abs(contractOpenGapDTO.openChange.floatValue())));
    }
}

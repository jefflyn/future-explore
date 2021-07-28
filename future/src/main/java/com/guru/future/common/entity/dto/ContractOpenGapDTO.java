package com.guru.future.common.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContractOpenGapDTO implements Comparable<ContractOpenGapDTO> {
    private String code;

    private String name;

    private String category;

    private BigDecimal preClose;

    private BigDecimal open;

    private BigDecimal gapRate;

    private String remark;


    @Override
    public int compareTo(ContractOpenGapDTO contractOpenGapDTO) {
        return this.gapRate.compareTo(contractOpenGapDTO.gapRate);
    }
}

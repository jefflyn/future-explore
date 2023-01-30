package com.guru.future.common.entity.query;

import lombok.Data;

import java.util.List;

/**
 * @author j
 * @date 2021/7/19 3:09 下午
 **/
@Data
public class ContractQuery {
    private String symbol;

    private List<String> codes;

}

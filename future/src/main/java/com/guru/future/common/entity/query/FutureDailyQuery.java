package com.guru.future.common.entity.query;

import lombok.Data;

import java.util.List;

/**
 * @author j
 * @date 2021/7/19 3:09 下午
 **/
@Data
public class FutureDailyQuery {
    private String symbol;

    private String code;

    private String tradeDate;

    private List<String> codes;

}

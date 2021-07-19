package com.guru.future.common.entity.query;

import lombok.Data;

import java.util.List;

/**
 * @author jeff
 * @date 2021/7/19 3:09 下午
 **/
@Data
public class FutureBasicQuery {
    private String symbol;

    private String name;

    private String type;

    private List<String> codes;

}

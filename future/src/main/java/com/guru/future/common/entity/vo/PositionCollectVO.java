package com.guru.future.common.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class PositionCollectVO  {
    private List<String> timeList;

    private List<Position> positionData;

    @Data
    public static class Position{
        private String name;

        private List<Integer> data;
    }
}
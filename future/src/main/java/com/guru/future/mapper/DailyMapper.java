package com.guru.future.mapper;

import com.guru.future.common.entity.dao.DailyDOWithBLOBs;

public interface DailyMapper {
    int insert(DailyDOWithBLOBs record);

    int insertSelective(DailyDOWithBLOBs record);
}
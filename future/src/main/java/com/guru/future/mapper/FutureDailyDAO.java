package com.guru.future.mapper;

import com.guru.future.domain.FutureDailyDO;

public interface FutureDailyDAO {
    int deleteByPrimaryKey(Long id);

    int insert(FutureDailyDO record);

    int insertSelective(FutureDailyDO record);

    FutureDailyDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FutureDailyDO record);

    int updateByPrimaryKey(FutureDailyDO record);
}
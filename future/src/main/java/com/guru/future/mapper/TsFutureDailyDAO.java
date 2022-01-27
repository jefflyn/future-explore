package com.guru.future.mapper;

import com.guru.future.domain.TsFutureDailyDO;

public interface TsFutureDailyDAO {
    int insert(TsFutureDailyDO record);

    int insertSelective(TsFutureDailyDO record);
}
package com.guru.future.mapper;

import com.guru.future.common.entity.query.FutureDailyQuery;
import com.guru.future.domain.TsFutureDailyDO;

import java.util.List;

public interface TsDailyDAO {
    int insert(TsFutureDailyDO record);

    List<TsFutureDailyDO> selectByQuery(FutureDailyQuery futureDailyQuery);
}
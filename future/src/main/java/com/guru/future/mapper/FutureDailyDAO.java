package com.guru.future.mapper;

import com.guru.future.common.entity.query.FutureDailyQuery;
import com.guru.future.domain.FutureDailyDO;

import java.util.List;

public interface FutureDailyDAO {
    int deleteByPrimaryKey(Long id);

    int insertSelective(FutureDailyDO record);

    FutureDailyDO selectByPrimaryKey(Long id);

    List<FutureDailyDO> selectByQuery(FutureDailyQuery query);

    int updateByCodeTradeDateSelective(FutureDailyDO record);

}
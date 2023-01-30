package com.guru.future.mapper;

import com.guru.future.common.entity.query.FutureDailyQuery;
import com.guru.future.common.entity.dao.FutureDailyDO;

import java.util.List;

public interface DailyDAO {
    int deleteByPrimaryKey(Long id);

    int insertSelective(FutureDailyDO futureDailyDO);

    FutureDailyDO selectByPrimaryKey(Long id);

    List<FutureDailyDO> selectByQuery(FutureDailyQuery query);

    int updateByCodeTradeDateSelective(FutureDailyDO futureDailyDO);

}
package com.guru.future.mapper;

import com.guru.future.common.entity.query.FutureDailyQuery;
import com.guru.future.common.entity.dao.TradeDailyDO;

import java.util.List;

public interface DailyMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TradeDailyDO tradeDailyDO);

    TradeDailyDO selectByPrimaryKey(Long id);

    List<TradeDailyDO> selectByQuery(FutureDailyQuery query);

    int updateByCodeTradeDateSelective(TradeDailyDO tradeDailyDO);

}
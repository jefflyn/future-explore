package com.guru.future.mapper;

import com.guru.future.common.entity.query.FutureDailyQuery;
import com.guru.future.common.entity.dao.FutureCollectDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CollectDAO {

    FutureCollectDO selectLastByCode(@Param("code") String code);

    List<FutureCollectDO> selectByQuery(FutureDailyQuery dailyQuery);

    int insertSelective(FutureCollectDO collectDO);

}
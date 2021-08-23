package com.guru.future.mapper;

import com.guru.future.domain.FutureDailyCollectDO;
import org.apache.ibatis.annotations.Param;

public interface FutureDailyCollectDAO {

    FutureDailyCollectDO selectLastByCode(@Param("code") String code);

    int insertSelective(FutureDailyCollectDO record);

}
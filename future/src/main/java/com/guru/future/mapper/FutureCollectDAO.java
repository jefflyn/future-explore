package com.guru.future.mapper;

import com.guru.future.domain.FutureCollectDO;
import org.apache.ibatis.annotations.Param;

public interface FutureCollectDAO {

    FutureCollectDO selectLastByCode(@Param("code") String code);

    int insertSelective(FutureCollectDO collectDO);

}
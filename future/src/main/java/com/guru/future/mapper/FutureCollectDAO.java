package com.guru.future.mapper;

import com.guru.future.domain.FutureCollectDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FutureCollectDAO {

    FutureCollectDO selectLastByCode(@Param("code") String code);

    List<FutureCollectDO> selectByCurrentDate();

    int insertSelective(FutureCollectDO collectDO);

}
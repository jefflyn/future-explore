package com.guru.future.mapper;

import com.guru.future.common.entity.dao.FutureLiveDO;

import java.util.List;

public interface LiveDAO {
    int deleteAll();

    int insertSelective(FutureLiveDO futureLiveDO);

    FutureLiveDO selectByPrimaryKey(String code);

    List<FutureLiveDO> selectAll();

    int updateByPrimaryKeySelective(FutureLiveDO futureLiveDO);

}
package com.guru.future.mapper;

import com.guru.future.domain.FutureLiveDO;

import java.util.List;

public interface FutureLiveDAO {
    int deleteByPrimaryKey(String code);

    int insertSelective(FutureLiveDO futureLiveDO);

    FutureLiveDO selectByPrimaryKey(String code);

    List<FutureLiveDO> selectAll();

    int updateByPrimaryKeySelective(FutureLiveDO futureLiveDO);

}
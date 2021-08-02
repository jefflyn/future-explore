package com.guru.future.mapper;

import com.guru.future.domain.FutureLiveDO;

public interface FutureLiveDAO {
    int deleteByPrimaryKey(String code);

    int insertSelective(FutureLiveDO record);

    FutureLiveDO selectByPrimaryKey(String code);

    int updateByPrimaryKeySelective(FutureLiveDO record);

}
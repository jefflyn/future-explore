package com.guru.future.mapper;

import com.guru.future.common.entity.dao.FutureBasicDO;

public interface FutureBasicDAO {
    int deleteByPrimaryKey(String symbol);

    int insert(FutureBasicDO record);

    int insertSelective(FutureBasicDO record);

    FutureBasicDO selectByPrimaryKey(String symbol);

    int updateByPrimaryKeySelective(FutureBasicDO record);

    int updateByPrimaryKey(FutureBasicDO record);
}
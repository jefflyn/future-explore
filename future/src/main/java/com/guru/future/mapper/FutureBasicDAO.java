package com.guru.future.mapper;

import com.guru.future.common.entity.query.FutureBasicQuery;
import com.guru.future.domain.FutureBasicDO;

import java.util.List;

public interface FutureBasicDAO {
    int deleteByPrimaryKey(String symbol);

    int insertSelective(FutureBasicDO record);

    FutureBasicDO selectByPrimaryKey(String symbol);

    int updateByPrimaryKeySelective(FutureBasicDO record);

    List<FutureBasicDO> selectByQuery(FutureBasicQuery query);
}
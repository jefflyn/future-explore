package com.guru.future.mapper;

import com.guru.future.common.entity.query.FutureBasicQuery;
import com.guru.future.common.entity.dao.FutureBasicDO;

import java.util.List;

public interface BasicDAO {
    FutureBasicDO selectBySymbol(String symbol);

    int updateByCodeSelective(FutureBasicDO futureBasicDO);

    List<FutureBasicDO> selectByQuery(FutureBasicQuery query);
}
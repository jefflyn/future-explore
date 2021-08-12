package com.guru.future.mapper;

import com.guru.future.common.entity.query.FutureBasicQuery;
import com.guru.future.domain.FutureBasicDO;

import java.util.List;

public interface FutureBasicDAO {
    int insertSelective(FutureBasicDO record);

    FutureBasicDO selectByCode(String code);

    int updateByCodeSelective(FutureBasicDO record);

    List<FutureBasicDO> selectByQuery(FutureBasicQuery query);
}
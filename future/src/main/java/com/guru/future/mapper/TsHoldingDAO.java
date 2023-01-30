package com.guru.future.mapper;

import com.guru.future.common.entity.dao.TsFutureHoldingDO;

public interface TsHoldingDAO {
    int insert(TsFutureHoldingDO record);

    int insertSelective(TsFutureHoldingDO record);
}
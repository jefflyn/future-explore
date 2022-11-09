package com.guru.future.mapper;

import com.guru.future.domain.TsFutureHoldingDO;

public interface TsHoldingDAO {
    int insert(TsFutureHoldingDO record);

    int insertSelective(TsFutureHoldingDO record);
}
package com.guru.future.mapper;

import com.guru.future.common.entity.query.FutureBasicQuery;
import com.guru.future.domain.TsFutureContractDO;

import java.util.List;

public interface TsContractDAO {
    int insert(TsFutureContractDO record);

    TsFutureContractDO selectByPrimaryKey(String tsCode);

    List<TsFutureContractDO> selectByQuery(FutureBasicQuery futureBasicQuery);

}
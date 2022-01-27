package com.guru.future.mapper;

import com.guru.future.domain.TsFutureContractDO;

public interface TsFutureContractDAO {
    int deleteByPrimaryKey(String tsCode);

    int insert(TsFutureContractDO record);

    int insertSelective(TsFutureContractDO record);

    TsFutureContractDO selectByPrimaryKey(String tsCode);

    int updateByPrimaryKeySelective(TsFutureContractDO record);

    int updateByPrimaryKey(TsFutureContractDO record);
}
package com.guru.future.mapper;

import com.guru.future.domain.ContractDO;

public interface ContractDAO {
    int deleteByPrimaryKey(String code);

    int insert(ContractDO record);

    int insertSelective(ContractDO record);

    ContractDO selectByPrimaryKey(String code);

    int updateByPrimaryKeySelective(ContractDO record);

    int updateByPrimaryKey(ContractDO record);
}
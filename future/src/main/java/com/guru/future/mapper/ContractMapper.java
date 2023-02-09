package com.guru.future.mapper;

import com.guru.future.common.entity.dao.ContractDO;
import com.guru.future.common.entity.query.ContractQuery;

import java.util.List;

public interface ContractMapper {
    int deleteByPrimaryKey(String code);

    ContractDO selectByPrimaryKey(String code);

    List<ContractDO> selectByQuery(ContractQuery query);

    int updateByPrimaryKeySelective(ContractDO record);

}
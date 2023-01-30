package com.guru.future.mapper;

import com.guru.future.common.entity.dao.ContractDO;
import com.guru.future.common.entity.dao.FutureBasicDO;
import com.guru.future.common.entity.query.ContractQuery;
import com.guru.future.common.entity.query.FutureBasicQuery;

import java.util.List;

public interface ContractDAO {
    int deleteByPrimaryKey(String code);

    int insert(ContractDO record);

    int insertSelective(ContractDO record);

    ContractDO selectByPrimaryKey(String code);

    List<ContractDO> selectByQuery(ContractQuery query);

    int updateByPrimaryKeySelective(ContractDO record);

    int updateByPrimaryKey(ContractDO record);
}
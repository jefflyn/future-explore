package com.guru.future.mapper;

import com.guru.future.domain.FutureLiveDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FutureLiveDAO {
    int deleteAll(@Param("codes") List<String> codes);

    int insertSelective(FutureLiveDO futureLiveDO);

    FutureLiveDO selectByPrimaryKey(String code);

    List<FutureLiveDO> selectAll();

    int updateByPrimaryKeySelective(FutureLiveDO futureLiveDO);

}
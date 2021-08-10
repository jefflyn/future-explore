package com.guru.future.mapper;

import com.guru.future.domain.OpenGapDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OpenGapDAO {
    int insertBatch(@Param("list") List<OpenGapDO> record);

    int insertSelective(OpenGapDO record);
}
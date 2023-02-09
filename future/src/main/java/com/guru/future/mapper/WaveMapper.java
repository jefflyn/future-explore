package com.guru.future.mapper;

import com.guru.future.common.entity.dao.WaveDO;

import java.util.List;

public interface WaveMapper {
    List<WaveDO> selectAll();
}
package com.guru.future.mapper;

import com.guru.future.domain.FutureLogDO;

public interface FutureLogDAO {
    int insert(FutureLogDO record);

    int insertSelective(FutureLogDO record);

}
package com.guru.future.mapper;

import com.guru.future.domain.FutureLogDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FutureLogDAO {

    int insertSelective(FutureLogDO futureLogDO);

    int delete(FutureLogDO futureLogDO);

    List<String> selectCodesByTradeDate(@Param("tradeDate") String tradeDate);

}
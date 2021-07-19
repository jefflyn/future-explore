package com.guru.future.common.entity.converter;

import java.math.BigDecimal;
import java.util.Date;

import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.domain.FutureLiveDO;

public class FutureLiveConverter {

    public static FutureLiveDO convertFromContractRealtimeDTO(ContractRealtimeDTO contractRealtimeDTO) {
        FutureLiveDO futureLiveDO = new FutureLiveDO();
        futureLiveDO.setCode(contractRealtimeDTO.getContractCode());
        futureLiveDO.setName(contractRealtimeDTO.getContractName());
        futureLiveDO.setPrice(contractRealtimeDTO.getPrice());
        futureLiveDO.setChange(new BigDecimal("0"));
        futureLiveDO.setBid1(contractRealtimeDTO.getBid());
        futureLiveDO.setAsk1(contractRealtimeDTO.getAsk());
        futureLiveDO.setLow(contractRealtimeDTO.getLow());
        futureLiveDO.setHigh(contractRealtimeDTO.getHigh());
        futureLiveDO.setPosition(new BigDecimal("0"));
        futureLiveDO.setAmp(new BigDecimal("0"));
        futureLiveDO.setWave("");
        futureLiveDO.setLowestChange(new BigDecimal("0"));
        futureLiveDO.setHighestChange(new BigDecimal("0"));
        futureLiveDO.setUpdateTime(new Date());
        return futureLiveDO;
    }
}

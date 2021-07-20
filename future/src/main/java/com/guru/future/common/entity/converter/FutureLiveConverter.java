package com.guru.future.common.entity.converter;

import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.domain.FutureLiveDO;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FutureLiveConverter {

    public static FutureLiveDO convertFromContractRealtimeDTO(ContractRealtimeDTO contractRealtimeDTO) {
        FutureLiveDO futureLiveDO = new FutureLiveDO();
        futureLiveDO.setCode(contractRealtimeDTO.getContractCode());
        futureLiveDO.setName(contractRealtimeDTO.getContractName());
        futureLiveDO.setPrice(contractRealtimeDTO.getPrice());
        BigDecimal change = (contractRealtimeDTO.getPrice().subtract(contractRealtimeDTO.getPreSettle())
                .multiply(BigDecimal.valueOf(100))
                .divide(contractRealtimeDTO.getPreSettle(), 2, RoundingMode.HALF_UP));
        futureLiveDO.setChange(change);
        futureLiveDO.setBid1(contractRealtimeDTO.getBid());
        futureLiveDO.setAsk1(contractRealtimeDTO.getAsk());
        futureLiveDO.setLow(contractRealtimeDTO.getLow());
        futureLiveDO.setHigh(contractRealtimeDTO.getHigh());
        BigDecimal position = BigDecimal.ZERO;
        if (contractRealtimeDTO.getHigh().compareTo(contractRealtimeDTO.getLow()) != 0) {
            position = (contractRealtimeDTO.getPrice().subtract(contractRealtimeDTO.getLow()))
                    .multiply(BigDecimal.valueOf(100))
                    .divide((contractRealtimeDTO.getHigh().subtract(contractRealtimeDTO.getLow())), 0, RoundingMode.HALF_UP);
        } else if (contractRealtimeDTO.getHigh().compareTo(contractRealtimeDTO.getLow()) == 0
                && contractRealtimeDTO.getLow().compareTo(contractRealtimeDTO.getPrice()) > 0) {
            position = BigDecimal.valueOf(100);
        }
        futureLiveDO.setPosition(position);
        futureLiveDO.setAmp((contractRealtimeDTO.getHigh().subtract(contractRealtimeDTO.getLow()))
                .multiply(BigDecimal.valueOf(100))
                .divide(contractRealtimeDTO.getHigh(), 2, RoundingMode.HALF_UP));
        futureLiveDO.setWave("");
        futureLiveDO.setLowestChange(new BigDecimal("0"));
        futureLiveDO.setHighestChange(new BigDecimal("0"));
        return futureLiveDO;
    }
}

package com.guru.future.common.entity.converter;

import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.enums.CollectType;
import com.guru.future.common.utils.FutureUtil;
import com.guru.future.domain.FutureCollectDO;
import com.guru.future.domain.FutureDailyDO;
import com.guru.future.domain.FutureLiveDO;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ContractRealtimeConverter {

    public static FutureLiveDO convert2LiveDO(ContractRealtimeDTO contractRealtimeDTO) {
        FutureLiveDO futureLiveDO = new FutureLiveDO();
        futureLiveDO.setCode(contractRealtimeDTO.getCode());
        futureLiveDO.setName(contractRealtimeDTO.getName());
        futureLiveDO.setPrice(contractRealtimeDTO.getPrice());
        futureLiveDO.setChange(FutureUtil.calcChange(contractRealtimeDTO.getPrice(), contractRealtimeDTO.getPreSettle()));
        futureLiveDO.setBid1(contractRealtimeDTO.getBid());
        futureLiveDO.setAsk1(contractRealtimeDTO.getAsk());
        futureLiveDO.setOpen(contractRealtimeDTO.getOpen());
        futureLiveDO.setLow(contractRealtimeDTO.getLow());
        futureLiveDO.setHigh(contractRealtimeDTO.getHigh());
        int position = FutureUtil.getPosition(contractRealtimeDTO.getPrice(), contractRealtimeDTO.getHigh(),
                contractRealtimeDTO.getLow());
        futureLiveDO.setPosition(position);
        futureLiveDO.setAmp((contractRealtimeDTO.getHigh().subtract(contractRealtimeDTO.getLow()))
                .multiply(BigDecimal.valueOf(100))
                .divide(contractRealtimeDTO.getHigh(), 2, RoundingMode.HALF_UP));
        futureLiveDO.setWave("");
        futureLiveDO.setLowestChange(new BigDecimal("0"));
        futureLiveDO.setHighestChange(new BigDecimal("0"));
        return futureLiveDO;
    }

    public static FutureDailyDO convert2DailyDO(ContractRealtimeDTO contractRealtimeDTO) {
        FutureDailyDO dailyDO = new FutureDailyDO();
        dailyDO.setSymbol(contractRealtimeDTO.getCode().replaceAll("[^A-Za-z]", ""));
        dailyDO.setTradeDate(contractRealtimeDTO.getTradeDate());

        dailyDO.setCode(contractRealtimeDTO.getCode());
        dailyDO.setName(contractRealtimeDTO.getName());
        dailyDO.setClose(contractRealtimeDTO.getPrice());
        BigDecimal change = (contractRealtimeDTO.getPrice().subtract(contractRealtimeDTO.getPreSettle())
                .multiply(BigDecimal.valueOf(100))
                .divide(contractRealtimeDTO.getPreSettle(), 2, RoundingMode.HALF_UP));
        dailyDO.setCloseChange(change);
        if (contractRealtimeDTO.getSettle() == null || BigDecimal.ZERO.compareTo(contractRealtimeDTO.getSettle()) == 0) {
            dailyDO.setSettle(contractRealtimeDTO.getAvgPrice());
        } else {
            dailyDO.setSettle(contractRealtimeDTO.getSettle());
        }
        BigDecimal settleChange = (contractRealtimeDTO.getPrice().subtract(contractRealtimeDTO.getPreSettle())
                .multiply(BigDecimal.valueOf(100))
                .divide(contractRealtimeDTO.getPreSettle(), 2, RoundingMode.HALF_UP));
        dailyDO.setSettleChange(settleChange);
        dailyDO.setOpen(contractRealtimeDTO.getOpen());
        dailyDO.setLow(contractRealtimeDTO.getLow());
        dailyDO.setHigh(contractRealtimeDTO.getHigh());
        dailyDO.setHlDiff(contractRealtimeDTO.getHigh().subtract(contractRealtimeDTO.getLow()));
        dailyDO.setAmplitude((contractRealtimeDTO.getHigh().subtract(contractRealtimeDTO.getLow()))
                .multiply(BigDecimal.valueOf(100))
                .divide(contractRealtimeDTO.getHigh(), 2, RoundingMode.HALF_UP));
        dailyDO.setPreSettle(contractRealtimeDTO.getPreSettle());
        dailyDO.setDealVol(contractRealtimeDTO.getDealVol());
        dailyDO.setHoldVol(contractRealtimeDTO.getHoldVol());
        dailyDO.setExchange(contractRealtimeDTO.getExchange());
        dailyDO.setRemark("");

        return dailyDO;
    }

    public static FutureCollectDO convert2DailyCollectDO(CollectType collectType, ContractRealtimeDTO contractRealtimeDTO) {
        FutureCollectDO collectDO = new FutureCollectDO();
        collectDO.setType(collectType.getId());
        collectDO.setTradeDate(contractRealtimeDTO.getTradeDate());
        collectDO.setCode(contractRealtimeDTO.getCode());
        collectDO.setName(contractRealtimeDTO.getName());
        collectDO.setPrice(contractRealtimeDTO.getPrice());
        collectDO.setPosition(FutureUtil.getPosition(contractRealtimeDTO.getPrice(),contractRealtimeDTO.getHigh(),
                contractRealtimeDTO.getLow()));
        collectDO.setHigh(contractRealtimeDTO.getHigh());
        collectDO.setLow(contractRealtimeDTO.getLow());
        collectDO.setDealVol(contractRealtimeDTO.getDealVol());
        collectDO.setHoldVol(contractRealtimeDTO.getHoldVol());
        collectDO.setRemark(collectType.getDesc());
        return collectDO;
    }
}

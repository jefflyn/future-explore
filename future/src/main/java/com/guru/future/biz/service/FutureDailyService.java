package com.guru.future.biz.service;

import com.guru.future.biz.manager.FutureBasicManager;
import com.guru.future.biz.manager.FutureDailyManager;
import com.guru.future.biz.manager.FutureSinaManager;
import com.guru.future.common.entity.converter.ContractRealtimeConverter;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.utils.DateUtil;
import com.guru.future.domain.FutureBasicDO;
import com.guru.future.domain.FutureDailyDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author j
 */
@Service
@Slf4j
public class FutureDailyService {
    @Resource
    private FutureBasicManager futureBasicManager;
    @Resource
    private FutureDailyManager futureDailyManager;
    @Resource
    private FutureSinaManager futureSinaManager;

    @Async
    public void addTradeDaily() {
        List<ContractRealtimeDTO> contractRealtimeDTOList = futureSinaManager.getAllRealtimeFromSina();
        this.upsertTradeDaily(contractRealtimeDTOList);
    }

    @Async
    public void upsertTradeDaily(List<ContractRealtimeDTO> contractRealtimeDTOList) {
        Map<String, FutureBasicDO> basicMap = futureBasicManager.getBasicMap();

        String tradeDate = DateUtil.currentDate();
        Map<String, FutureDailyDO> lastDailyMap;
        if (DateUtil.isNight()) {
            tradeDate = DateUtil.getNextTradeDate(tradeDate);
            lastDailyMap = futureDailyManager.getFutureDailyMap(DateUtil.currentDate(), new ArrayList<>(basicMap.keySet()));
        } else {
            lastDailyMap = futureDailyManager.getFutureDailyMap(DateUtil.getLastTradeDate(tradeDate), new ArrayList<>(basicMap.keySet()));
        }
        Map<String, FutureDailyDO> existedDailyMap = futureDailyManager.getFutureDailyMap(tradeDate, new ArrayList<>(basicMap.keySet()));
        for (ContractRealtimeDTO contractRealtimeDTO : contractRealtimeDTOList) {
            FutureDailyDO futureDailyDO = ContractRealtimeConverter.convert2DailyDO(contractRealtimeDTO);
            FutureDailyDO existedDailyDO = existedDailyMap.get(futureDailyDO.getCode());
            if (existedDailyDO != null) {
                if (!futureDailyDO.changFlag().equals(existedDailyDO.changFlag())) {
                    futureDailyManager.updateFutureDaily(futureDailyDO);
                }
            } else {
                FutureDailyDO lastDailyDO = lastDailyMap.get(futureDailyDO.getCode());
                if (lastDailyDO != null && futureDailyDO.getTradeDate().compareTo(lastDailyDO.getTradeDate()) > 0) {
                    futureDailyDO.setPreClose(lastDailyDO.getClose());
                    futureDailyDO.setPreSettle(lastDailyDO.getSettle());
                }
                log.warn("futureDailyDO.getTradeDate()={}, lastDailyDO={}", futureDailyDO.getTradeDate(), lastDailyDO);
                futureDailyManager.addFutureDaily(futureDailyDO);
            }
        }
    }
}

package com.guru.future.biz.service;

import com.guru.future.biz.manager.FutureBasicManager;
import com.guru.future.biz.manager.FutureDailyManager;
import com.guru.future.biz.manager.FutureLiveManager;
import com.guru.future.common.entity.converter.ContractRealtimeConverter;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.domain.FutureBasicDO;
import com.guru.future.domain.FutureDailyDO;
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
public class FutureDailyService {
    @Resource
    private FutureBasicManager futureBasicManager;
    @Resource
    private FutureDailyManager futureDailyManager;
    @Resource
    private FutureLiveManager futureLiveManager;

    @Async
    public void addTradeDaily(String tradeDate, List<ContractRealtimeDTO> contractRealtimeDTOList) {
        Map<String, FutureBasicDO> basicMap = futureBasicManager.getBasicMap();
        Map<String, FutureDailyDO> dailyMap = futureDailyManager.getFutureDailyMap(tradeDate, new ArrayList<>(basicMap.keySet()));

        for (ContractRealtimeDTO contractRealtimeDTO : contractRealtimeDTOList) {
            FutureDailyDO futureDailyDO = ContractRealtimeConverter.convert2DailyDO(contractRealtimeDTO);
            FutureDailyDO existedDailyDO = dailyMap.get(futureDailyDO.getCode());
            if (existedDailyDO != null) {
                futureDailyDO.setId(existedDailyDO.getId());
                futureDailyManager.updateFutureDaily(futureDailyDO);
            } else {
                futureDailyManager.addFutureDaily(futureDailyDO);
            }
        }
    }
}

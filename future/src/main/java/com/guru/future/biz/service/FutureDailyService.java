package com.guru.future.biz.service;

import com.google.common.collect.Lists;
import com.guru.future.biz.manager.FutureBasicManager;
import com.guru.future.biz.manager.FutureDailyManager;
import com.guru.future.biz.manager.FutureLiveManager;
import com.guru.future.common.entity.converter.ContractRealtimeConverter;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.utils.DateUtil;
import com.guru.future.domain.FutureBasicDO;
import com.guru.future.domain.FutureDailyDO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    public void addTradeDaily(List<ContractRealtimeDTO> contractRealtimeDTOList) {
        Map<String, FutureBasicDO> basicMap = futureBasicManager.getBasicMap();
        Map<String, FutureDailyDO> dailyMap = futureDailyManager.getFutureDailyMap(DateUtil.currentTradeDate(), Lists.newArrayList(basicMap.keySet()));

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

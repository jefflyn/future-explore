package com.guru.future.biz.service;

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
import java.util.ArrayList;
import java.util.Date;
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
    public void upsertTradeDaily(List<ContractRealtimeDTO> contractRealtimeDTOList) {
        Date date = new Date();
        String tradeDate = DateUtil.isNight() ? DateUtil.getNextTradeDate(date) : DateUtil.currentDate();
        Map<String, FutureBasicDO> basicMap = futureBasicManager.getBasicMap();
        Map<String, FutureDailyDO> updateDailyMap = futureDailyManager.getFutureDailyMap(tradeDate, new ArrayList<>(basicMap.keySet()));
        Map<String, FutureDailyDO> currentDailyMap = futureDailyManager.getFutureDailyMap(DateUtil.currentDate(), new ArrayList<>(basicMap.keySet()));


        for (ContractRealtimeDTO contractRealtimeDTO : contractRealtimeDTOList) {
            FutureDailyDO futureDailyDO = ContractRealtimeConverter.convert2DailyDO(contractRealtimeDTO);
            FutureDailyDO existedDailyDO = updateDailyMap.get(futureDailyDO.getCode());
            if (existedDailyDO != null) {
                futureDailyDO.setId(existedDailyDO.getId());
                /**
                 * after morning close: update pre_close
                 * after noon close: update pre_close
                 */
                if (!futureDailyDO.toString().equals(existedDailyDO.toString())) {
                    futureDailyManager.updateFutureDaily(futureDailyDO);
                }
            } else {
                currentDailyMap.get(futureDailyDO.getCode());
                futureDailyManager.addFutureDaily(futureDailyDO);
            }
        }
    }
}

package com.guru.future.biz.service;

import com.guru.future.biz.manager.FutureBasicManager;
import com.guru.future.biz.manager.FutureDailyCollectManager;
import com.guru.future.biz.manager.FutureDailyManager;
import com.guru.future.biz.manager.FutureSinaManager;
import com.guru.future.common.entity.converter.ContractRealtimeConverter;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.enums.DailyCollectType;
import com.guru.future.common.utils.DateUtil;
import com.guru.future.domain.FutureBasicDO;
import com.guru.future.domain.FutureDailyCollectDO;
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
public class FutureDailyCollectService {
    @Resource
    private FutureSinaManager futureSinaManager;
    @Resource
    private FutureDailyCollectManager dailyCollectManager;

    @Async
    public void addTradeDailyCollect(DailyCollectType collectType) {
        if(!DateUtil.isTradeTime()){
//            return;
        }
        List<ContractRealtimeDTO> contractRealtimeDTOList = futureSinaManager.getAllRealtimeFromSina();
        for (ContractRealtimeDTO contractRealtimeDTO : contractRealtimeDTOList) {
            FutureDailyCollectDO dailyCollectDO = ContractRealtimeConverter.convert2DailyCollectDO(collectType, contractRealtimeDTO);
            dailyCollectManager.addDailyCollect(dailyCollectDO);
            log.info("数据采集成功={}", dailyCollectDO);
        }
    }
}

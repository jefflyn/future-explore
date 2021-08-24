package com.guru.future.biz.service;

import com.guru.future.biz.manager.FutureDailyCollectManager;
import com.guru.future.biz.manager.FutureLogManager;
import com.guru.future.common.entity.converter.ContractRealtimeConverter;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.enums.DailyCollectType;
import com.guru.future.common.utils.DateUtil;
import com.guru.future.domain.FutureDailyCollectDO;
import com.guru.future.task.DailyCollectTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author j
 */
@Service
@Slf4j
public class FutureDailyCollectService {
    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(2, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("collectSchedule-" + t.getId());
            t.setDaemon(true);
            return t;
        }
    });

    @Resource
    private FutureLogManager futureLogManager;

    @Resource
    private FutureDailyCollectManager dailyCollectManager;

    /**
     * 延时任务每5分钟采集数据
     *
     * @param codes
     * @param collectType
     */
    public void scheduleTradeDailyCollect(List<String> codes, DailyCollectType collectType) {
        if (!DateUtil.isTradeTime()) {
            return;
        }
        if (CollectionUtils.isEmpty(codes)) {
            return;
        }
        dailyCollectManager.setCollectType(collectType);
        dailyCollectManager.setCollectCodes(codes);
        this.executorService.scheduleWithFixedDelay(new DailyCollectTask(dailyCollectManager),
                5L, 5L, TimeUnit.MINUTES);
    }

    public void addTradeDailyCollect(DailyCollectType collectType) {
        List<String> codes = new ArrayList<>(); //futureLogManager.getLogCodes();
        List<ContractRealtimeDTO> realtimeDTOList;
        if (CollectionUtils.isEmpty(codes)) {
            realtimeDTOList = dailyCollectManager.getFutureSinaManager().getAllRealtimeFromSina();
        } else {
            realtimeDTOList = dailyCollectManager.getFutureSinaManager().getRealtimeFromSina(codes);
        }
        if (CollectionUtils.isEmpty(realtimeDTOList)) {
            return;
        }
        dailyCollectManager.setCollectType(collectType);
        for (ContractRealtimeDTO contractRealtimeDTO : realtimeDTOList) {
            FutureDailyCollectDO dailyCollectDO = ContractRealtimeConverter.convert2DailyCollectDO(dailyCollectManager.getCollectType(), contractRealtimeDTO);
            dailyCollectManager.addDailyCollect(dailyCollectDO);
            log.info("data collect success! {}", dailyCollectDO);
        }
    }
}

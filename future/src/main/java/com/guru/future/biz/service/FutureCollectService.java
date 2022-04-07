package com.guru.future.biz.service;

import com.guru.future.biz.manager.FutureCollectManager;
import com.guru.future.common.entity.converter.ContractRealtimeConverter;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.enums.CollectType;
import com.guru.future.common.utils.DateUtil;
import com.guru.future.domain.FutureCollectDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * @author j
 */
@Service
@Slf4j
public class FutureCollectService {
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
    private FutureCollectManager dailyCollectManager;

    /**
     * 延时任务每5分钟采集数据
     *
     * @param codes
     * @param collectType
     */
    public void scheduleTradeDailyCollect(List<String> codes, CollectType collectType) {
        if (CollectionUtils.isEmpty(codes)) {
            return;
        }
        dailyCollectManager.setCollectType(collectType);
        dailyCollectManager.setCollectCodes(codes);
//        this.executorService.scheduleWithFixedDelay(new DailyCollectTask(dailyCollectManager),
//                5L, 10L, TimeUnit.MINUTES);
    }

    public void addTradeDailyCollect(CollectType collectType) {
        if (Boolean.FALSE.equals(DateUtil.isTradeTime())){
            log.warn("not trade time, data collect end!!!");
            return;
        }
        List<ContractRealtimeDTO> realtimeDTOList= dailyCollectManager.getFutureSinaManager().getAllRealtimeFromSina();
        if (CollectionUtils.isEmpty(realtimeDTOList)) {
            return;
        }
        dailyCollectManager.setCollectType(collectType);
        for (ContractRealtimeDTO contractRealtimeDTO : realtimeDTOList) {
            FutureCollectDO dailyCollectDO = ContractRealtimeConverter.convert2DailyCollectDO(dailyCollectManager.getCollectType(), contractRealtimeDTO);
            dailyCollectManager.addDailyCollect(dailyCollectDO);
//            log.info("data collect success! {}", contractRealtimeDTO);
        }
    }

}
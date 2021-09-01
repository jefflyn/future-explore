package com.guru.future.task;

import com.guru.future.biz.manager.FutureDailyCollectManager;
import com.guru.future.biz.service.FutureDailyCollectService;
import com.guru.future.common.entity.converter.ContractRealtimeConverter;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.enums.DailyCollectType;
import com.guru.future.common.utils.DateUtil;
import com.guru.future.domain.FutureDailyCollectDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author j
 * @date 2021/7/29 9:17 下午
 **/
@Slf4j
public class DailyCollectTask implements Runnable {
    private FutureDailyCollectManager dailyCollectManager;

    public DailyCollectTask(FutureDailyCollectManager dailyCollectManager) {
        this.dailyCollectManager = dailyCollectManager;
    }

    @Override
    public void run() {
        if (!DateUtil.isTradeTime()) {
            log.info("DailyCollectTask not in trade time, end!");
            throw new RuntimeException("DailyCollectTask not in trade time exception, end!");
        }
        try {
            List<ContractRealtimeDTO> realtimeDTOList = dailyCollectManager.getFutureSinaManager().getRealtimeFromSina(dailyCollectManager.getCollectCodes());
            if (CollectionUtils.isEmpty(realtimeDTOList)) {
                return;
            }
            for (ContractRealtimeDTO contractRealtimeDTO : realtimeDTOList) {
                FutureDailyCollectDO dailyCollectDO = ContractRealtimeConverter.convert2DailyCollectDO(dailyCollectManager.getCollectType(), contractRealtimeDTO);
                FutureDailyCollectDO lastDailyDO = dailyCollectManager.getLastDailyByCode(dailyCollectDO.getCode());
                if (lastDailyDO == null || DateUtil.diff(lastDailyDO.getCreateTime(), new Date(), TimeUnit.MINUTES) > 5) {
                    dailyCollectManager.addDailyCollect(dailyCollectDO);
                    log.info("[DailyCollectTask] success! {}", dailyCollectDO);
                }
            }
        } catch (Exception e) {
            log.error("[DailyCollectTask] failed to collect data!", e);
        }
    }
}


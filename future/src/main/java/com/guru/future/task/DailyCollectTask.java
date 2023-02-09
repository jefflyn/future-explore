package com.guru.future.task;

import com.guru.future.biz.manager.FutureCollectManager;
import com.guru.future.common.entity.converter.ContractRealtimeConverter;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.utils.FutureDateUtil;
import com.guru.future.common.entity.dao.FutureCollectDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author j
 * @date 2021/7/29 9:17 下午
 **/
@Slf4j
public class DailyCollectTask implements Runnable {
    private FutureCollectManager dailyCollectManager;

    public DailyCollectTask(FutureCollectManager dailyCollectManager) {
        this.dailyCollectManager = dailyCollectManager;
    }

    @Override
    public void run() {
        if (!FutureDateUtil.isTradeTime()) {
            log.info("DailyCollectTask not in trade time, end!");
            throw new RuntimeException("DailyCollectTask not in trade time exception, end!");
        }
        try {
            List<ContractRealtimeDTO> realtimeDTOList = dailyCollectManager.getFutureSinaManager().getRealtimeFromSina(dailyCollectManager.getCollectCodes());
            if (CollectionUtils.isEmpty(realtimeDTOList)) {
                return;
            }
            for (ContractRealtimeDTO contractRealtimeDTO : realtimeDTOList) {
                FutureCollectDO dailyCollectDO = ContractRealtimeConverter.convert2DailyCollectDO(dailyCollectManager.getCollectType(), contractRealtimeDTO);
                FutureCollectDO lastDailyDO = dailyCollectManager.getLastDailyByCode(dailyCollectDO.getCode());
                if (lastDailyDO == null || FutureDateUtil.diff(lastDailyDO.getCreateTime(), new Date(), TimeUnit.MINUTES) > 5) {
                    dailyCollectManager.addDailyCollect(dailyCollectDO);
                    log.info("[DailyCollectTask] success! {}", dailyCollectDO);
                }
            }
        } catch (Exception e) {
            log.error("[DailyCollectTask] failed to collect data!", e);
        }
    }
}


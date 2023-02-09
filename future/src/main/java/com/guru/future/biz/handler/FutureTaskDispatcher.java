package com.guru.future.biz.handler;

import com.guru.future.biz.manager.ContractManager;
import com.guru.future.biz.manager.remote.FutureSinaManager;
import com.guru.future.biz.service.FutureLiveService;
import com.guru.future.common.cache.PriceFlashCache;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.ui.FutureFrame;
import com.guru.future.common.utils.FutureDateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author j
 * @date 2021/7/19 5:31 下午
 **/
@Service
@Slf4j
public class FutureTaskDispatcher {
    public static Boolean REFRESH = false;
    private Boolean keepRunning = false;

    @Resource
    private ContractManager contractManager;
    @Resource
    private FutureSinaManager futureSinaManager;
    @Resource
    private FutureLiveService futureLiveService;

    public Boolean stopRunning() {
        return keepRunning = false;
    }

    public static void setRefresh() {
        REFRESH = true;
    }

    public void executePulling(Boolean refresh) throws InterruptedException {
        if (keepRunning && Boolean.FALSE.equals(refresh)) {
            log.warn("realtime is on-live!");
            return;
        }
        PriceFlashCache.deleteAll();
        keepRunning = true;
        LongAdder times = new LongAdder();
        REFRESH = refresh == null ? false : refresh;
        List<String> codeList = contractManager.getContractCodes();
        log.info(">>> smell the coffee, let's get this party started!");
        futureLiveService.refreshLiveData();
        while (keepRunning) {
            if (REFRESH) {
                codeList = contractManager.getContractCodes();
                FutureDateUtil.TRADE_TIME_TEST = true;
            } else {
                FutureDateUtil.TRADE_TIME_TEST = false;
            }
            if (CollectionUtils.isEmpty(codeList)) {
                return;
            }
            if (!FutureDateUtil.isTradeTime() && Boolean.FALSE.equals(REFRESH)) {
                log.info(">>> music off, party over!");
                times.increment();
                if (times.intValue() > 5) {
                    keepRunning = false;
                    break;
                } else {
                    TimeUnit.SECONDS.sleep(1L);
                    continue;
                }
            }
            List<ContractRealtimeDTO> contractRealtimeDTOList = futureSinaManager.getRealtimeFromSina(codeList);
            // async live data
            futureLiveService.refreshLiveData(contractRealtimeDTOList, REFRESH);
            FutureFrame futureFrame = FutureFrame.buildFutureFrame(futureLiveService.getMarketOverview().toString());
            futureFrame.createMsgFrame(null);
            REFRESH = false;
            TimeUnit.SECONDS.sleep(2L);
        }
    }
}

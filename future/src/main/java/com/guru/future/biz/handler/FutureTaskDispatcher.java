package com.guru.future.biz.handler;

import com.guru.future.biz.manager.FutureBasicManager;
import com.guru.future.biz.manager.FutureSinaManager;
import com.guru.future.biz.service.FutureDailyService;
import com.guru.future.biz.service.FutureLiveService;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author j
 * @date 2021/7/19 5:31 下午
 **/
@Service
@Slf4j
public class FutureTaskDispatcher {
    public static Boolean REFRESH = false;
    private Boolean keepRunning = true;

    @Resource
    private FutureBasicManager futureBasicManager;

    @Resource
    private FutureSinaManager futureSinaManager;

    @Resource
    private FutureLiveService futureLiveService;

    @Resource
    private FutureDailyService futureDailyService;

    public Boolean stopRunning() {
        return keepRunning = false;
    }

    public static void setRefresh() {
        REFRESH = true;
    }

    @Async
    public void executePulling(Boolean refresh) throws InterruptedException {
        keepRunning = true;
        REFRESH = refresh == null ? false : refresh;
        List<String> codeList = futureBasicManager.getAllCodes();
        while (keepRunning) {
            if (DateUtil.isSysBreakTime()) {
//                continue;
            }
            if (REFRESH) {
                codeList = futureBasicManager.getAllCodes();
            }
            if (CollectionUtils.isEmpty(codeList)) {
                return;
            }
            List<ContractRealtimeDTO> contractRealtimeDTOList = futureSinaManager.getRealtimeFromSina(codeList);

            // async live data
            futureLiveService.refreshLiveData(contractRealtimeDTOList);

            if (DateUtil.isNight()) {
                futureDailyService.upsertTradeDaily(contractRealtimeDTOList);
            }

            // async log
        }
        TimeUnit.SECONDS.sleep(2L);
    }
}

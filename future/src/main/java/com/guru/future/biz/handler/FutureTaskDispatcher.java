package com.guru.future.biz.handler;

import com.google.common.base.Strings;
import com.guru.future.biz.manager.FutureBasicManager;
import com.guru.future.biz.manager.FutureSinaManager;
import com.guru.future.biz.service.FutureDailyService;
import com.guru.future.biz.service.FutureLiveService;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.utils.DateUtil;
import com.guru.future.common.utils.SinaHqUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author j
 * @date 2021/7/19 5:31 下午
 **/
@Service
@Slf4j
public class FutureTaskDispatcher {
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

    @Async
    public void executePulling(Boolean refresh) throws InterruptedException {
        keepRunning = true;
        List<String> codeList = futureBasicManager.getAllCodes();
        while (keepRunning) {
            if (DateUtil.isSysBreakTime()) {
                continue;
            }
            if (ObjectUtils.defaultIfNull(refresh, false)) {
                codeList = futureBasicManager.getAllCodes();
            }
            List<String> contractList = futureSinaManager.fetchContractInfo(codeList);
            if (!CollectionUtils.isEmpty(codeList)) {
                List<ContractRealtimeDTO> contractRealtimeDTOList = new ArrayList<>();
                for (String contract : contractList) {
                    if (Strings.isNullOrEmpty(contract)) {
                        continue;
                    }
//                    System.out.println(JSON.toJSONString();
                    ContractRealtimeDTO contractRealtimeDTO = ContractRealtimeDTO.convertFromHqList(SinaHqUtil.parse2List(contract));
                    contractRealtimeDTOList.add(contractRealtimeDTO);
                    if (contractRealtimeDTOList.size() == RandomUtils.nextInt(1, 1000)) {
                        log.info(contract);
                    }
                }
                // async live data
                futureLiveService.refreshLiveData(contractRealtimeDTOList);

                // async daily data
                futureDailyService.upsertTradeDaily(contractRealtimeDTOList);

                // async log
            }
            TimeUnit.SECONDS.sleep(2L);
        }
    }
}

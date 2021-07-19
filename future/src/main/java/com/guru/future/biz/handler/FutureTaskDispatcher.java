package com.guru.future.biz.handler;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.guru.future.biz.service.FutureLiveService;
import com.guru.future.biz.manager.FutureBasicManager;
import com.guru.future.biz.manager.FutureSinaManager;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.utils.SinaHqUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author j
 * @date 2021/7/19 5:31 下午
 **/
@Service
public class FutureTaskDispatcher {
    private Boolean keepRunning = true;

    @Resource
    private FutureBasicManager futureBasicManager;

    @Resource
    private FutureSinaManager futureSinaManager;

    @Resource
    private FutureLiveService futureLiveService;

    public Boolean stopRunning() {
        return keepRunning = false;
    }

    @Async
    public void executePulling() {
        keepRunning = true;
        List<String> codeList = futureBasicManager.getAllCodes();
        while (keepRunning) {
            List<String> contractList = futureSinaManager.fetchContractInfo(codeList);
            if (!CollectionUtils.isEmpty(codeList)) {
                for (String contract : contractList) {
                    if (Strings.isNullOrEmpty(contract)) {
                        continue;
                    }
                    System.out.println(contract);
                    System.out.println(JSON.toJSONString(ContractRealtimeDTO.convertFromHqList(SinaHqUtil.parse2List(contract))));

                    // async live data

                    // async daily data

                    // async log

                }
            }
        }
    }
}

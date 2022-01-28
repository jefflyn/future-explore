package com.guru.future.biz.service;

import com.google.common.base.Splitter;
import com.guru.future.biz.manager.TsFutureBasicManager;
import com.guru.future.biz.manager.TsFutureDailyManager;
import com.guru.future.biz.manager.remote.TsFutureManager;
import com.guru.future.common.entity.converter.FutureDailyConverter;
import com.guru.future.common.enums.ContractType;
import com.guru.future.domain.TsFutureDailyDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author j
 */
@Service
@Slf4j
public class TsFutureDailyService {
    @Resource
    private TsFutureManager tsFutureManager;

    @Resource
    private TsFutureBasicManager tsFutureBasicManager;

    @Resource
    private TsFutureDailyManager tsFutureDailyManager;

    public Boolean batchAddDaily(String tsCodeStr, String startDate, String endDate) {
        List<String> tsCodes;
        if (Strings.isNotBlank(tsCodeStr)) {
            tsCodes = Splitter.on(",").splitToList(tsCodeStr);
        } else {
            tsCodes = tsFutureBasicManager.getTsCodeByType(ContractType.CONTINUE.getCode());
        }
        log.info("batchAddDaily total={}, tsCodes={}", tsCodes.size(), tsCodes);
        for (String tsCode : tsCodes) {
            asyncAddFutureBasic(tsCode, startDate, endDate);
        }
        return true;
    }

    @Async
    public void asyncAddFutureBasic(String tsCode, String startDate, String endDate) {
        log.info("tsCode={}, start={}, end={} asyncAddFutureBasic start", tsCode, startDate, endDate);
        try {
            String result = tsFutureManager.getDaily(tsCode, startDate, endDate);
            List<TsFutureDailyDO> futureDailyDOList = FutureDailyConverter.toTsFutureDailyDO(result);
            tsFutureDailyManager.batchAddFutureBasic(futureDailyDOList);
        } catch (Exception e) {
            log.info("get {} ts daily error: ", tsCode, e);
        }
    }
}

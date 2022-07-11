package com.guru.future.biz.service;

import com.google.common.base.Splitter;
import com.guru.future.biz.manager.FutureBasicManager;
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
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author j
 */
@Service
@Slf4j
public class TsFutureDailyService {
    @Resource
    private FutureBasicManager futureBasicManager;

    @Resource
    private TsFutureManager tsFutureManager;

    @Resource
    private TsFutureBasicManager tsFutureBasicManager;

    @Resource
    private TsFutureDailyManager tsFutureDailyManager;

    public Boolean batchAddDaily(String tsCodeStr, String startDate, String endDate) {
        List<String> tsCodes;
        if (Strings.isNotBlank(tsCodeStr)) {
            if ("C".equals(tsCodeStr.toUpperCase(Locale.ROOT))) {
                tsCodes = tsFutureBasicManager.getTsCodeByType(ContractType.CONTINUE.getCode());
            } else if ("M".equals(tsCodeStr.toUpperCase(Locale.ROOT))) {
                tsCodes = tsFutureBasicManager.getTsCodeByType(ContractType.MAIN.getCode());
            } else if ("R".equals(tsCodeStr.toUpperCase(Locale.ROOT))) {
                tsCodes = futureBasicManager.getAll().stream().map(e -> e.getCode() + "." + e.getExchange()).collect(Collectors.toList());
            } else {
                tsCodes = Splitter.on(",").splitToList(tsCodeStr);
            }
        } else {
            tsCodes = tsFutureBasicManager.getTsCodeByType(ContractType.CONTINUE.getCode());
            tsCodes.addAll(tsFutureBasicManager.getTsCodeByType(ContractType.MAIN.getCode()));
            List<String> mainCodes = futureBasicManager.getAll().stream().map(e -> e.getCode() + "." + e.getExchange()).collect(Collectors.toList());
            tsCodes.addAll(mainCodes);
        }
        for (String tsCode : tsCodes) {
            asyncAddFutureDaily(tsCode, startDate, endDate);
        }
        log.info("batchAddDaily done! total={}, tsCodes={}", tsCodes.size(), tsCodes);
        return true;
    }

    @Async
    public void asyncAddFutureDaily(String tsCode, String startDate, String endDate) {
        try {
            String result = tsFutureManager.getDaily(tsCode, startDate, endDate);
            log.info("tsCode={}, start={}, end={}, asyncAddFutureDaily result={}", tsCode, startDate, endDate, result);
            List<TsFutureDailyDO> futureDailyDOList = FutureDailyConverter.toTsFutureDailyDO(result);
            tsFutureDailyManager.batchAddFutureDaily(futureDailyDOList);
        } catch (Exception e) {
            log.info("get {} ts daily error: ", tsCode, e);
        }
    }
}

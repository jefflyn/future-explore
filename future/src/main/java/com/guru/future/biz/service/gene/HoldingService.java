package com.guru.future.biz.service.gene;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.guru.future.biz.manager.FutureBasicManager;
import com.guru.future.biz.manager.TsFutureHoldingManager;
import com.guru.future.biz.manager.remote.TsFutureManager;
import com.guru.future.common.entity.converter.FutureHoldingConverter;
import com.guru.future.domain.FutureBasicDO;
import com.guru.future.domain.TsFutureHoldingDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author j
 */
@Service
@Slf4j
public class HoldingService {
    @Resource
    private TsFutureManager tsFutureManager;

    @Resource
    private FutureBasicManager futureBasicManager;

    @Resource
    private TsFutureHoldingManager tsFutureHoldingManager;

    public Boolean batchAddHolding(String codeStr, String startDate, String endDate) {
        List<String> codes;
        if (Strings.isNotBlank(codeStr)) {
            codes = Splitter.on(",").splitToList(codeStr);
        } else {
            codes = futureBasicManager.getAll().stream().map(FutureBasicDO::getCode).collect(Collectors.toList());
        }
        List<List<String>> codeList = Lists.partition(codes, 20);
        for (List<String> list : codeList) {
            for (String code : list) {
                asyncAddFutureHolding(code, startDate, endDate);
            }
            try {
                System.out.println("wait for 1 minute...");
                TimeUnit.MINUTES.sleep(1L);
            } catch (Exception e) {
            }
        }
        log.info("batchAddHolding done! total={}, codes={}", codes.size(), codes);
        return true;
    }

    @Async
    public void asyncAddFutureHolding(String code, String startDate, String endDate) {
        try {
            String result = tsFutureManager.getHolding(code, startDate, endDate);
            log.info("code={}, start={}, end={} asyncAddFutureHolding result={}", code, startDate, endDate, result);
            List<TsFutureHoldingDO> futureDailyDOList = FutureHoldingConverter.toTsFutureHoldingDO(result);
            tsFutureHoldingManager.batchAddFutureHolding(futureDailyDOList);
        } catch (Exception e) {
            log.info("get {} ts holding error: ", code, e.getMessage());
        }
    }
}

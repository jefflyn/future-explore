package com.guru.future.schedule;

import com.guru.future.biz.service.FutureDailyService;
import com.guru.future.biz.service.TsFutureDailyService;
import com.guru.future.biz.service.gene.HoldingService;
import com.guru.future.common.utils.FutureDateUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;

/**
 * @author j
 * @date 2021/7/29 9:17 下午
 **/

@Configuration
@EnableScheduling
public class DailyUpdateJob {
    @Resource
    private FutureDailyService futureDailyService;

    @Resource
    private TsFutureDailyService tsFutureDailyService;

    @Resource
    private HoldingService holdingService;

//    @Scheduled(cron = "8 8 15,03 * * MON-SAT")
    public void updateTradeDaily() {
        futureDailyService.addTradeDaily();
    }

//    @Scheduled(cron = "0 30 17,18 * * MON-FRI")
    public void updateTsDaily() {
        String startDate = FutureDateUtil.latestTradeDate(FutureDateUtil.TRADE_DATE_PATTERN_FLAT);
        String endDate = FutureDateUtil.latestTradeDate(FutureDateUtil.TRADE_DATE_PATTERN_FLAT);
        tsFutureDailyService.batchAddDaily(null, startDate, endDate);

        holdingService.batchAddHolding(null, startDate, endDate);
    }
}

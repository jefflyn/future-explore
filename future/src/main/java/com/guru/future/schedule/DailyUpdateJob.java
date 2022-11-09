package com.guru.future.schedule;

import com.guru.future.biz.service.FutureDailyService;
import com.guru.future.biz.service.TsFutureDailyService;
import com.guru.future.biz.service.gene.HoldingService;
import com.guru.future.common.utils.DateUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

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

    @Scheduled(cron = "8 8 15,03 * * MON-SAT")
    public void updateTradeDaily() {
        futureDailyService.addTradeDaily();
    }

    @Scheduled(cron = "0 30 17,18 * * MON-FRI")
    public void updateTsDaily() {
        String startDate = DateUtil.latestTradeDate(DateUtil.TRADE_DATE_PATTERN_FLAT);
        String endDate = DateUtil.latestTradeDate(DateUtil.TRADE_DATE_PATTERN_FLAT);
        tsFutureDailyService.batchAddDaily(null, startDate, endDate);

        holdingService.batchAddHolding(null, startDate, endDate);
    }
}

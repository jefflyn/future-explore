package com.guru.future.task;

import com.guru.future.biz.handler.FutureTaskDispatcher;
import com.guru.future.biz.service.FutureDailyService;
import com.guru.future.biz.service.FutureGapService;
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
public class FutureTask {
    @Resource
    private FutureDailyService futureDailyService;
    @Resource
    private FutureGapService futureGapService;
    @Resource
    private FutureTaskDispatcher futureTaskDispatcher;

    @Scheduled(cron = "0 0 9,13,21 * * ?")
    private void realtime() throws InterruptedException {
        futureTaskDispatcher.executePulling(false);
    }

    @Scheduled(cron = "0 8 15,03 * * ?")
    private void updateTradeDaily() {
        futureDailyService.addTradeDaily();
    }

    @Scheduled(cron = "6 59 08 * * MON-FRI")
    private void monitorOpenGap() {
        futureGapService.monitorOpenGap();
    }

    @Scheduled(cron = "10,15,20,30 59 20 * * MON-FRI")
    private void monitorPreNightOpenGap() {
        futureGapService.monitorOpenGap();
    }

    @Scheduled(cron = "3 0 21 * * MON-FRI")
    private void monitorNightOpenGap() {
        futureGapService.monitorOpenGap();
    }

}

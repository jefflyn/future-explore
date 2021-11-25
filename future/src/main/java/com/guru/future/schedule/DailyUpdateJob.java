package com.guru.future.schedule;

import com.guru.future.biz.service.FutureDailyService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
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

    @Async
    @Scheduled(cron = "8 8 15,03 * * MON-SAT")
    public void updateTradeDaily() {
        futureDailyService.addTradeDaily();
    }

}

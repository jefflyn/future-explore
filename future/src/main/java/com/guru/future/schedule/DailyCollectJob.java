package com.guru.future.schedule;

import com.guru.future.biz.service.FutureCollectService;
import com.guru.future.common.enums.CollectType;
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
public class DailyCollectJob {
    @Resource
    private FutureCollectService dailyCollectService;

    @Async
    @Scheduled(cron = "6 0,5,10,15,20,25,30,35,40,45,50,55,59 9,10,11,13,14 * * MON-FRI")
    public void dailyCollect() {
        dailyCollectService.addTradeDailyCollect(CollectType.COLLECT_TIMED);
    }

}
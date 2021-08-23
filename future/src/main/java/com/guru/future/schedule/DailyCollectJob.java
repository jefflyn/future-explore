package com.guru.future.schedule;

import com.guru.future.biz.service.FutureDailyCollectService;
import com.guru.future.common.enums.DailyCollectType;
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
public class DailyCollectJob {
    @Resource
    private FutureDailyCollectService dailyCollectService;

    @Scheduled(cron = "0 30 9,10,11 * * ?")
    private void dailyCollect1() {
        dailyCollectService.addTradeDailyCollect(DailyCollectType.COLLECT_TIMED);

    }

    @Scheduled(cron = "0 0 3,14,15,22,23 * * ?")
    private void dailyCollect2() {
        dailyCollectService.addTradeDailyCollect(DailyCollectType.COLLECT_TIMED);
    }

}

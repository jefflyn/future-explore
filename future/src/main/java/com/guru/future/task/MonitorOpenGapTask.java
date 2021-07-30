package com.guru.future.task;

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
public class MonitorOpenGapTask {
    @Resource
    private FutureDailyService futureDailyService;
    @Resource
    private FutureGapService futureGapService;

    @Scheduled(cron = "5 59 8,20 * * ?")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)
    private void monitorOpenGap() {
        futureGapService.monitorOpenGap();
    }

    @Scheduled(cron = "0 1 3,12,15,23 * * ?")
    private void updateTradeDaily() {
        futureDailyService.addTradeDaily();
    }
}

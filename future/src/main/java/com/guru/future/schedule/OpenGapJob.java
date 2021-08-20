package com.guru.future.schedule;

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
public class OpenGapJob {
    @Resource
    private FutureGapService futureGapService;

    @Scheduled(cron = "8 59 08 * * MON-FRI")
    private void monitorOpenGap() {
        futureGapService.monitorOpenGap();
    }

    @Scheduled(cron = "3 0 21 * * MON-FRI")
    private void monitorNightOpenGap() {
        futureGapService.monitorOpenGap();
    }

}

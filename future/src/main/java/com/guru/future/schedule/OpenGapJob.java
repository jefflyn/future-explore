package com.guru.future.schedule;

import com.guru.future.biz.service.FutureGapService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
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
@Slf4j
public class OpenGapJob {
    @Resource
    private FutureGapService futureGapService;

    @Async
    @Scheduled(cron = "0 0 9,21 * * MON-FRI")
    public void monitorOpenGap() throws InterruptedException {
        log.info("open gap job start ...");
        futureGapService.monitorOpenGap();
    }

}

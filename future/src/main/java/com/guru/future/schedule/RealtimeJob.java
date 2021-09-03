package com.guru.future.schedule;

import com.guru.future.biz.handler.FutureTaskDispatcher;
import com.guru.future.biz.service.FutureDailyService;
import com.guru.future.biz.service.FutureGapService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RealtimeJob {
    @Resource
    private FutureTaskDispatcher futureTaskDispatcher;

    @Scheduled(cron = "4 0 9,21 * * ?")
    private void realtime1() throws InterruptedException {
        log.info("realtime start...");
        futureTaskDispatcher.executePulling(false);
    }

    @Scheduled(cron = "3 30 13 * * ?")
    private void realtime2() throws InterruptedException {
        log.info("realtime start...");
        futureTaskDispatcher.executePulling(false);
    }

}

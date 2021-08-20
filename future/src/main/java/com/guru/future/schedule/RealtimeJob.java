package com.guru.future.schedule;

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
public class RealtimeJob {
    @Resource
    private FutureTaskDispatcher futureTaskDispatcher;

    @Scheduled(cron = "3 0 9,21 * * ?")
    private void realtime1() throws InterruptedException {
        futureTaskDispatcher.executePulling(false);
    }

    @Scheduled(cron = "56 29 13 * * ?")
    private void realtime2() throws InterruptedException {
        futureTaskDispatcher.executePulling(false);
    }
}

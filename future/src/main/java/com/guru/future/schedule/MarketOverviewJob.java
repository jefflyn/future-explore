package com.guru.future.schedule;

import com.guru.future.biz.service.FutureLiveService;
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
public class MarketOverviewJob {
    @Resource
    private FutureLiveService futureLiveService;

    @Scheduled(cron = "0 30 9,21 * * MON-FRI")
    public void sendMarketOverview() throws Exception {
        log.info("send market overview task start...");
        futureLiveService.sendMarketOverviewMail();
    }

}

package com.guru.future.schedule.job;

import com.guru.future.biz.service.FutureLiveService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import javax.annotation.Resource;

/**
 * @author j
 * @date 2021/7/29 9:17 下午
 **/

@Slf4j
public class MarketOverviewJob implements Job {
    @Resource
    private FutureLiveService futureLiveService;

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.info("send market overview task start...");
//        futureLiveService.sendMarketOverviewMail();
    }
}

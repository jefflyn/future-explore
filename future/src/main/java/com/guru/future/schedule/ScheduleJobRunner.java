package com.guru.future.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class ScheduleJobRunner implements CommandLineRunner {

    @Resource
    public CronScheduleJobs scheduleJobs;

    @Override
    public void run(String... args) throws Exception {
        scheduleJobs.scheduleJobs();
        log.info(">>>>>>>>>>>>>>>定时任务调度开启<<<<<<<<<<<<<");
    }
}

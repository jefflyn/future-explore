package com.guru.future.schedule;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ScheduleJobRunner implements CommandLineRunner {
    @Resource
    public CronScheduleTrigger scheduleJobs;

    @Override
    public void run(String... args) throws Exception {
        scheduleJobs.scheduleJobs();
    }
}

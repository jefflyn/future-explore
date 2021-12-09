package com.guru.future.schedule;

import com.guru.future.schedule.job.MarketOverviewJob;
import com.guru.future.schedule.job.OpenGapJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CronScheduleJobs {
    @Resource
    private SchedulerFactoryBean schedulerFactoryBean;

    public void scheduleJobs() throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        openGapJob(scheduler);
        marketOverviewJob(scheduler);
    }

    private void openGapJob(Scheduler scheduler) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(OpenGapJob.class).withIdentity("OpenGapJob", "group1").build();
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("3 0 9,21 ? * 2-6");
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("OpenGapJobTrigger", "group1").withSchedule(scheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
    }

    private void marketOverviewJob(Scheduler scheduler) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(MarketOverviewJob.class).withIdentity("MarketOverviewJob", "group1").build();
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 10 9,21,22 ? * 2-6");
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("MarketOverviewTrigger", "group1").withSchedule(scheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
    }

}

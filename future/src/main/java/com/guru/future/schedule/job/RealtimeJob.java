package com.guru.future.schedule.job;

import cn.hutool.core.map.MapUtil;
import com.guru.future.biz.handler.FutureTaskDispatcher;
import com.guru.future.biz.service.FutureCollectService;
import com.guru.future.biz.service.FutureLiveService;
import com.guru.future.common.entity.vo.FutureOverviewVO;
import com.guru.future.common.enums.CollectType;
import com.guru.future.common.utils.DateUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author j
 * @date 2021/7/29 9:17 下午
 **/

@Slf4j
public class RealtimeJob implements Job {
    @Resource
    private FutureTaskDispatcher futureTaskDispatcher;

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        futureTaskDispatcher.executePulling(false);
    }

}

package com.guru.future.schedule.job;

import cn.hutool.core.map.MapUtil;
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
public class OverviewCollectJob implements Job {
    @Resource
    private FutureLiveService futureLiveService;

    @Resource
    private FutureCollectService dailyCollectService;

    @Resource
    private RedissonClient redissonClient;

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        if (Boolean.FALSE.equals(DateUtil.isTradeTime())) {
            log.warn("OverviewCollectJob not in trade time, end ...");
            return;
        }
        dailyCollect();
        log.info("OverviewCollectJob start ...");
        FutureOverviewVO futureOverviewVO = futureLiveService.getMarketOverview();
        String key = DateUtil.currentTradeDate() + "_overview";
        RList<Map<String, String>> cacheList = redissonClient.getList(key);
        cacheList.expire(30, TimeUnit.DAYS);
        String overviewDesc = futureOverviewVO.getTotalAvgChangeStr();
        if (overviewDesc == null) {
            return;
        }
        cacheList.add(MapUtil.of(DateUtil.toHourMinute(new Date()), overviewDesc));
        System.out.println("Market Overview >>>");
        for (Map<String, String> map : cacheList) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }
    }

    @Async("bizAsyncTaskExecutor")
    public void dailyCollect(){
        log.info("DailyCollectJob start ...");
        dailyCollectService.addTradeDailyCollect(CollectType.COLLECT_TIMED);
    }
}

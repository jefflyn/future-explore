package com.guru.future.schedule;

import cn.hutool.core.map.MapUtil;
import com.guru.future.biz.service.FutureCollectService;
import com.guru.future.biz.service.FutureLiveService;
import com.guru.future.common.entity.vo.FutureOverviewVO;
import com.guru.future.common.enums.CollectType;
import com.guru.future.common.utils.DateUtil;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @author j
 * @date 2021/7/29 9:17 下午
 **/
@Configuration
@EnableScheduling
public class DailyCollectJob {
    @Resource
    private FutureCollectService dailyCollectService;
    @Resource
    private FutureLiveService futureLiveService;
    @Resource
    private RedissonClient redissonClient;

    @Scheduled(cron = "3 0,5,10,15,20,25,30,35,40,45,50,55 9,10,14 * * MON-FRI")
    public void dailyCollect1() {
        dailyCollectService.addTradeDailyCollect(CollectType.COLLECT_TIMED);
        overviewCollect();
    }

    @Scheduled(cron = "3 0,5,10,15,20,25,30 11 * * MON-FRI")
    public void dailyCollect2() {
        dailyCollectService.addTradeDailyCollect(CollectType.COLLECT_TIMED);
        overviewCollect();
    }

    @Scheduled(cron = "3 30,35,40,45,50,55 13 * * MON-FRI")
    public void dailyCollect3() {
        dailyCollectService.addTradeDailyCollect(CollectType.COLLECT_TIMED);
        overviewCollect();
    }

    private void overviewCollect(){
        FutureOverviewVO futureOverviewVO = futureLiveService.getMarketOverview();
        String key = DateUtil.currentTradeDate() + "_Overview";
        RList<Map<String, String>> cacheList = redissonClient.getList(key);
        cacheList.add(MapUtil.of(DateUtil.toHourMinute(new Date()), futureOverviewVO.getTotalAvgChangeStr()));
        System.out.println("Market Overview >>>");
        for (Map<String, String> map : cacheList) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }
    }
}
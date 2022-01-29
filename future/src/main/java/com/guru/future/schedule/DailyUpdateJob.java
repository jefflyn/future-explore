package com.guru.future.schedule;

import com.guru.future.biz.service.FutureDailyService;
import com.guru.future.biz.service.TsFutureDailyService;
import com.guru.future.common.utils.DateUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

/**
 * @author j
 * @date 2021/7/29 9:17 下午
 **/

@Configuration
@EnableScheduling
public class DailyUpdateJob {
    @Resource
    private FutureDailyService futureDailyService;

    @Resource
    private TsFutureDailyService tsFutureDailyService;

    @Async
    @Scheduled(cron = "8 8 15,03 * * MON-SAT")
    public void updateTradeDaily() {
        futureDailyService.addTradeDaily();
    }

    @Async
    @Scheduled(cron = "0 30 12,16,17 * * MON-SAT")
    public void updateTsDaily() {
        String startDate = DateUtil.getLastTradeDate(null, DateUtil.TRADE_DATE_PATTERN_FLAT);
        String endDate = DateUtil.latestTradeDate(DateUtil.TRADE_DATE_PATTERN_FLAT);
        tsFutureDailyService.batchAddDaily(null, startDate, endDate);
        String mainCodes = "A2205.DCE,AG2205.INE,AL2203.SHFE,AP2205.CZCE,AU2206.SHFE,B2203.DCE,BU2206.SHFE,C2205.DCE,CF2205.CZCE,CJ2205.CZCE,CU2203.SHFE,EB2203.DCE,EG2205.DCE,FG2209.CZCE,FU2205.SHFE,HC2205.SHFE,I2205.DCE,J2205.DCE,JD2205.DCE,JM2205.DCE,L2205.DCE,LH2203.DCE,LU2203.INE,M2205.DCE,MA2205.CZCE,NI2203.SHFE,NR2204.INE,OI2205.CZCE,P2205.DCE,PB2203.SHFE,PF2205.CZCE,PG2203.DCE,PK2204.CZCE,PP2205.DCE,RB2205.SHFE,RM2205.CZCE,RU2209.SHFE,SA2209.CZCE,SC2203.INE,SF2205.CZCE,SM2205.CZCE,SN2203.SHFE,SP2206.SHFE,SR2205.CZCE,SS2203.SHFE,TA2205.CZCE,UR2205.CZCE,V2205.DCE,Y2205.DCE,ZC2205.CZCE,ZN2203.SHFE";
        tsFutureDailyService.batchAddDaily(mainCodes, startDate, endDate);

    }
}

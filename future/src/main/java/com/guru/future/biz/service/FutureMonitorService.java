package com.guru.future.biz.service;

import com.google.common.collect.Lists;
import com.guru.future.biz.manager.FutureLogManager;
import com.guru.future.common.enums.DailyCollectType;
import com.guru.future.common.utils.DateUtil;
import com.guru.future.common.utils.PriceFlashCache;
import com.guru.future.common.utils.WindowUtil;
import com.guru.future.domain.FutureLiveDO;
import com.guru.future.domain.FutureLogDO;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FutureMonitorService {
    private static List<Pair<Integer, Float>> MONITOR_PARAMS = new ArrayList<>();

    static {
//        MONITOR_PARAMS.add(Pair.with(30, 0.33F));
        MONITOR_PARAMS.add(Pair.with(60, 0.66F));
        MONITOR_PARAMS.add(Pair.with(120, 1F));
    }

    @Resource
    private FutureDailyCollectService collectService;

    @Resource
    private FutureLogManager futureLogManager;

    public void monitorPriceFlash(FutureLiveDO futureLiveDO, String histHighLowFlag) {
        if (DateUtil.isPriceMonitorTime()) {
            try {
                for (Pair<Integer, Float> param : MONITOR_PARAMS) {
                    triggerPriceFlash(param, futureLiveDO, histHighLowFlag);
                }
            } catch (Exception e) {
                log.error("monitorPriceFlash fail, error={}", e);
            }
        }
    }

    @Async
    @Transactional
    public void addPositionLog(FutureLiveDO futureLiveDO) {
        int position = -1;
        if (futureLiveDO.getPosition().compareTo(BigDecimal.ZERO) <= 0) {
            position = 0;
        } else if (futureLiveDO.getPosition().compareTo(BigDecimal.valueOf(100)) >= 0) {
            position = 100;
        }
        if (position > -1) {
            FutureLogDO futureLogDO = new FutureLogDO();
            futureLogDO.setTradeDate(DateUtil.currentTradeDate());
            futureLogDO.setCode(futureLiveDO.getCode());
            futureLogDO.setFactor(0);
            futureLogDO.setDiff(BigDecimal.ZERO);
            futureLogDO.setName(futureLiveDO.getName());
            if (position == 0) {
                futureLogDO.setType("日内低点");
                futureLogDO.setContent("日内低点");
                futureLogDO.setOption("做空");
            } else {
                futureLogDO.setType("日内高点");
                futureLogDO.setContent("日内高点");
                futureLogDO.setOption("做多");
            }
            futureLogDO.setSuggest(futureLiveDO.getPrice());
            futureLogDO.setPctChange(futureLiveDO.getChange());
            futureLogDO.setPosition(futureLiveDO.getPosition().intValue());
            this.msgNotice(position == 100, futureLogDO);
            futureLogManager.deleteLogByType(futureLogDO.getCode(), futureLogDO.getTradeDate(), futureLogDO.getType());
            futureLogManager.addFutureLog(futureLogDO);
            log.info("add log:{}", futureLogDO);
        }
    }

    @Async
    public void triggerPriceFlash(Pair<Integer, Float> param, FutureLiveDO futureLiveDO, String histHighLowFlag) {
        int factor = param.getValue0();
        float triggerDiff = param.getValue1();
        String code = futureLiveDO.getCode();
        String cachedKey = code + param;
        BigDecimal price = futureLiveDO.getPrice();
        PriceFlashCache.rPush(cachedKey, price);
        int priceLen = PriceFlashCache.length(cachedKey);
        if (priceLen == 1) {
//            log.info("[monitorPriceFlash] start! param={}, code={}", param, cachedKey);
            return;
        }
        int steps = factor / 2;
        BigDecimal lastPrice;
        if (priceLen >= steps) {
            lastPrice = PriceFlashCache.lPop(cachedKey);
        } else {
            lastPrice = PriceFlashCache.peekFirst(cachedKey);
        }
//        float diff = 0.0F; //Math.abs((price.subtract(lastPrice)).multiply(BigDecimal.valueOf(100)).divide(lastPrice, 2, RoundingMode.HALF_UP).floatValue());
        Pair<BigDecimal, BigDecimal> minMaxPrices = PriceFlashCache.getMinAndMaxFromList(cachedKey);
        BigDecimal minPrice = minMaxPrices.getValue0();
        BigDecimal maxPrice = minMaxPrices.getValue1();
        boolean isTrigger = false;
        String blastTip = "";
        float diff = (price.subtract(maxPrice)).multiply(BigDecimal.valueOf(100))
                .divide(maxPrice, 2, RoundingMode.HALF_UP).floatValue();
        if (Math.abs(diff) >= triggerDiff) {
            isTrigger = true;
            lastPrice = maxPrice;
            if (PriceFlashCache.index(cachedKey, maxPrice) > (priceLen / 2)) {
                blastTip = "+";
            }
        } else {
            diff = (price.subtract(minPrice)).multiply(BigDecimal.valueOf(100))
                    .divide(minPrice, 2, RoundingMode.HALF_UP).floatValue();
            if (Math.abs(diff) >= triggerDiff) {
                isTrigger = true;
                lastPrice = minPrice;
                if (PriceFlashCache.index(cachedKey, minPrice) > (priceLen / 2)) {
                    blastTip = "+";
                }
            }
        }

        if (isTrigger) {
            boolean isUp = price.compareTo(lastPrice) > 0;
            BigDecimal suggestPrice = lastPrice;
            String logType = (isUp ? "上涨" : "下跌") + blastTip;
            String suggestParam = (isUp ? "做多" : "做空");
            StringBuilder content = new StringBuilder();
            content.append(lastPrice).append("-").append(price);
            FutureLogDO futureLogDO = new FutureLogDO();
            futureLogDO.setTradeDate(DateUtil.currentTradeDate());
            futureLogDO.setCode(futureLiveDO.getCode());
            futureLogDO.setFactor(factor);
            futureLogDO.setDiff(BigDecimal.valueOf(diff));
            futureLogDO.setOption(suggestParam);
            futureLogDO.setName(futureLiveDO.getName());
            futureLogDO.setType(logType);
            futureLogDO.setContent(content.toString());
            futureLogDO.setSuggest(suggestPrice);
            futureLogDO.setPctChange(futureLiveDO.getChange());
            futureLogDO.setPosition(futureLiveDO.getPosition().intValue());
            futureLogDO.setRemark(logType + " " + histHighLowFlag);
            this.msgNotice(isUp, futureLogDO);
            futureLogManager.addFutureLog(futureLogDO);
            log.info("add log:{}", futureLogDO);
            collectService.scheduleTradeDailyCollect(Lists.newArrayList(code), DailyCollectType.COLLECT_SCHEDULE);
            // 删除价格列表，重新获取
            PriceFlashCache.delete(cachedKey);
        }
    }

    private void msgNotice(boolean isUp, FutureLogDO futureLogDO) {
        String diffStr = String.format("%.2f", futureLogDO.getDiff()) + "%";
        StringBuilder content = new StringBuilder();
        content.append(futureLogDO.getType()).append(" ").append(futureLogDO.getFactor())
                .append(" ").append(diffStr)
                .append("【").append(futureLogDO.getContent()).append("】")
                .append(futureLogDO.getOption()).append(" ").append(futureLogDO.getSuggest())
                .append(" ").append(futureLogDO.getPctChange()).append("%")
                .append(" ").append(futureLogDO.getPosition());
        // show msg frame
        WindowUtil.createMsgFrame(futureLogDO.getCode(), isUp, DateUtil.currentTime() + " "
                + futureLogDO.getName() + " " + content);
    }
}

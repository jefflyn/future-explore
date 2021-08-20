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
import org.apache.logging.log4j.util.Strings;
import org.javatuples.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
        MONITOR_PARAMS.add(Pair.with(10, 0.2F));
        MONITOR_PARAMS.add(Pair.with(30, 0.33F));
        MONITOR_PARAMS.add(Pair.with(50, 0.46F));
    }

    @Resource
    private FutureDailyCollectService collectService;

    @Resource
    private FutureLogManager futureLogManager;

    public void monitorPriceFlash(FutureLiveDO futureLiveDO, String histHighLowFlag) {
        try {
            for (Pair<Integer, Float> param : MONITOR_PARAMS) {
                triggerPriceFlash(param, futureLiveDO, histHighLowFlag);
            }
        } catch (Exception e) {
            log.error("monitorPriceFlash fail, error={}", e);
        }
    }

    @Async
    public void triggerPriceFlash(Pair<Integer, Float> param, FutureLiveDO futureLiveDO, String histHighLowFlag) {
        int factor = param.getValue0();
        float triggerDiff = param.getValue1();
        String key = futureLiveDO.getCode();
        BigDecimal price = futureLiveDO.getPrice();
        PriceFlashCache.rPush(key, price);
        int priceLen = PriceFlashCache.length(key);
        if (priceLen == 1) {
            log.info("[monitorPriceFlash] start! param={}, code={}", param, key);
            return;
        }
        int steps = factor / 5;
        BigDecimal lastPrice;
        if (priceLen >= steps) {
            lastPrice = PriceFlashCache.lPop(key);
        } else {
            lastPrice = PriceFlashCache.peekFirst(key);
        }
        float diff = Math.abs((price.subtract(lastPrice)).multiply(BigDecimal.valueOf(100))
                .divide(lastPrice, 2, RoundingMode.HALF_UP).floatValue());
        String blastTip = "";
        {
            if (priceLen > steps / 2) {
                // 取FIFO队列后半价格
                Pair<BigDecimal, BigDecimal> latePrices = PriceFlashCache.getMinAndMaxFromList(key,
                        Integer.valueOf(steps / 2), PriceFlashCache.length(key));
                BigDecimal minPrice = latePrices.getValue0();
                BigDecimal maxPrice = latePrices.getValue1();

                float diffMin;
                float diffMax = (price.subtract(maxPrice)).multiply(BigDecimal.valueOf(100))
                        .divide(maxPrice, 2, RoundingMode.HALF_UP).floatValue();
                if (Math.abs(diffMax) >= triggerDiff) {
                    blastTip = "+";
                    diff = diffMax;
                    lastPrice = maxPrice;
                }
                if (minPrice.compareTo(maxPrice) != 0) {
                    diffMin = (price.subtract(minPrice)).multiply(BigDecimal.valueOf(100))
                            .divide(minPrice, 2, RoundingMode.HALF_UP).floatValue();
                    if (Math.abs(diffMin) >= triggerDiff) {
                        blastTip = "+";
                        diff = diffMin;
                        lastPrice = minPrice;
                    }
                }
            }
        }

        if (diff >= triggerDiff || Strings.isNotBlank(blastTip)) {
            boolean isUp = price.compareTo(lastPrice) > 0;
            BigDecimal suggestPrice = (lastPrice.add(price)).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
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
            collectService.scheduleTradeDailyCollect(Lists.newArrayList(key), DailyCollectType.COLLECT_SCHEDULE);
            // 删除价格列表，重新获取
            PriceFlashCache.delete(key);
        }
    }

    private void msgNotice(boolean isUp, FutureLogDO futureLogDO) {
        String diffStr = String.format("%.2f", futureLogDO.getDiff()) + "%";
        StringBuilder content = new StringBuilder();
        content.append(futureLogDO.getType()).append(diffStr)
                .append("【").append(futureLogDO.getContent()).append("】")
                .append(futureLogDO.getOption()).append(" ").append(futureLogDO.getSuggest())
                .append(" ").append(futureLogDO.getPctChange()).append("%")
                .append(" ").append(futureLogDO.getPosition());
        // show msg frame
        WindowUtil.createMsgFrame(futureLogDO.getCode(), isUp, DateUtil.currentTime() + " "
                + futureLogDO.getName() + " " + content);
    }
}

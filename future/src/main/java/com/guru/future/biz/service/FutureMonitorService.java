package com.guru.future.biz.service;

import com.guru.future.biz.manager.FutureLogManager;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.utils.PriceFlashCache;
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
import java.util.Date;

@Service
@Slf4j
public class FutureMonitorService {
    private static final float TRIGGER_DIFF = 0.33F;

    @Resource
    private FutureLogManager futureLogManager;

    @Async
    public void triggerPriceFlash(FutureLiveDO futureLiveDO, String histHighLowFlag) {
        String key = futureLiveDO.getCode();
        BigDecimal price = futureLiveDO.getPrice();
        int secs = 50;
        PriceFlashCache.rPush(key, price);
        int priceLen = PriceFlashCache.length(key);
        int steps = secs / 5;
        BigDecimal lastPrice = null;
        if (priceLen >= steps) {
            lastPrice = PriceFlashCache.lPop(key);
        } else {
            lastPrice = PriceFlashCache.peekFirst(key);
        }
        String blastTip = "";
        if (priceLen > steps / 2) {
            // 取FIFO队列后半价格
            Pair<BigDecimal, BigDecimal> latePrices = PriceFlashCache.getMinAndMaxFromList(key, Integer.valueOf(steps / 2), priceLen);
            BigDecimal minPrice = latePrices.getValue0();
            BigDecimal maxPrice = latePrices.getValue1();

            float diffMin = 0F;
            float diffMax = Math.abs((price.subtract(maxPrice)).multiply(BigDecimal.valueOf(100))
                    .divide(maxPrice, 2, RoundingMode.HALF_UP).floatValue());
            if (minPrice.compareTo(maxPrice) != 0) {
                diffMin = Math.abs((price.subtract(minPrice)).multiply(BigDecimal.valueOf(100))
                        .divide(minPrice, 2, RoundingMode.HALF_UP).floatValue());
            }
            if (diffMax >= TRIGGER_DIFF || diffMin >= TRIGGER_DIFF) {
                blastTip = "￥";
            }
        }
        float diff = Math.abs((price.subtract(lastPrice)).multiply(BigDecimal.valueOf(100))
                .divide(lastPrice, 2, RoundingMode.HALF_UP).floatValue());

        if (diff >= TRIGGER_DIFF || Strings.isNotBlank(blastTip)) {
            String diffStr = diff + "%";
            boolean isUp = price.compareTo(lastPrice) > 0;
            BigDecimal suggestPrice = (lastPrice.add(price)).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
            String logType = isUp ? "上涨" : "下跌";
            String suggestParam = isUp ? "看多" : "看空" + suggestPrice;
            StringBuilder content = new StringBuilder(secs + "秒");
            content.append(blastTip + logType).append(diffStr)
                    .append("【").append(lastPrice).append("-").append(price).append("】")
                    .append(suggestParam);
            FutureLogDO futureLogDO = new FutureLogDO();
            futureLogDO.setName(futureLiveDO.getName());
            futureLogDO.setType(logType);
            futureLogDO.setContent(content.toString());
            futureLogDO.setPrice(price);
            futureLogDO.setPctChange(futureLiveDO.getChange());
            futureLogDO.setPosition(futureLiveDO.getPosition().intValue());
            futureLogDO.setLogTime(new Date());
            futureLogDO.setRemark(logType + histHighLowFlag);
            futureLogManager.addFutureLog(futureLogDO);
            // 删除价格列表，重新获取
            PriceFlashCache.delete(key);
        }
    }

}

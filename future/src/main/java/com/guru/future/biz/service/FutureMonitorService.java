package com.guru.future.biz.service;

import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.utils.PriceFlashCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class FutureMonitorService {

    public void triggerPriceFlash(ContractRealtimeDTO realtimeDTO) {
        String key = realtimeDTO.getCode();
        BigDecimal price = realtimeDTO.getPrice();
        int secs = 50;
        BigDecimal triggerDiff = BigDecimal.valueOf(0.33);
        PriceFlashCache.rPush(key, price);
        int priceLen = PriceFlashCache.length(key);
        int steps = secs / 5;
        BigDecimal lastPrice = null;
        if (priceLen >= steps) {
            lastPrice = PriceFlashCache.lPop(key);
        } else {
            lastPrice = PriceFlashCache.peekFirst(key);
        }
        if (priceLen > steps / 2) {

        }
    }

}

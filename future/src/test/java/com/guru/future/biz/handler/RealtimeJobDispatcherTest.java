package com.guru.future.biz.handler;

import com.guru.future.TestBase;
import com.guru.future.biz.manager.FutureSinaManager;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.cache.PriceFlashCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RealtimeJobDispatcherTest extends TestBase {

    @Resource
    private FutureSinaManager futureSinaManager;
    @Resource
    private FutureTaskDispatcher futureTaskDispatcher;

    @Test
    public void testExecutePulling() {
        try {
            futureTaskDispatcher.executePulling(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPriceCached() throws InterruptedException {
        log.info("testPriceCached start >>> ");
        int i = 0;
        long start = System.currentTimeMillis();
        while (true) {
            List<ContractRealtimeDTO> contractRealtimeDTOList = futureSinaManager.getAllRealtimeFromSina();
            for (ContractRealtimeDTO contractRealtimeDTO : contractRealtimeDTOList) {
                PriceFlashCache.rPush(contractRealtimeDTO.getCode(),
                        contractRealtimeDTO.getPrice().add(BigDecimal.valueOf(RandomUtils.nextInt(1, 1000))));
            }
            i += 1;
            log.info("testPriceCached try times={}", i);
            for (String key : PriceFlashCache.PRICE_QUEUE.keySet()) {
                log.info("{}: size={} {}", key, PriceFlashCache.PRICE_QUEUE.get(key).size(),
                        PriceFlashCache.PRICE_QUEUE.get(key));
            }
            TimeUnit.SECONDS.sleep(2L);
            long diff = (System.currentTimeMillis() - start) / 1000L;
            if (diff >= 60) {
                log.info("testPriceCached end! try times=" + i);
                break;
            }
        }
    }
}
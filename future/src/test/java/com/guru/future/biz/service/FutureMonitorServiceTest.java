package com.guru.future.biz.service;

import java.math.BigDecimal;

import com.guru.future.biz.manager.FutureLogManager;
import com.guru.future.common.utils.PriceFlashCache;
import com.guru.future.domain.FutureLiveDO;
import org.javatuples.Pair;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PriceFlashCache.class})
public class FutureMonitorServiceTest extends PowerMockTestCase {
    @InjectMocks
    private FutureMonitorService futureMonitorService;
    @Mock
    private FutureLogManager futureLogManager;

    @BeforeClass
    public void beforeClass() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void triggerPriceFlash1() {
        FutureLiveDO futureLiveDO = new FutureLiveDO();
        futureLiveDO.setCode("OI2201");
        futureLiveDO.setName("OI2201");
        futureLiveDO.setPrice(new BigDecimal("1000"));
        futureLiveDO.setChange(new BigDecimal("1.23"));
        futureLiveDO.setLow(new BigDecimal("980"));
        futureLiveDO.setHigh(new BigDecimal("1033"));
        futureLiveDO.setPosition(new BigDecimal("10"));
        when(futureLogManager.addFutureLog(any())).thenReturn(true);
        String histHighLowFlag = " ";
        futureMonitorService.triggerPriceFlash(futureLiveDO, histHighLowFlag);
    }

    @Test
    void triggerPriceFlash2() {
        FutureLiveDO futureLiveDO = new FutureLiveDO();
        futureLiveDO.setCode("OI2201");
        futureLiveDO.setName("OI2201");
        futureLiveDO.setPrice(new BigDecimal("1000"));
        futureLiveDO.setChange(new BigDecimal("1.23"));
        futureLiveDO.setLow(new BigDecimal("980"));
        futureLiveDO.setHigh(new BigDecimal("1033"));
        futureLiveDO.setPosition(new BigDecimal("10"));
        PowerMockito.mockStatic(PriceFlashCache.class);
        when(PriceFlashCache.length(any())).thenReturn(10);
        when(PriceFlashCache.lPop(any())).thenReturn(BigDecimal.valueOf(1033));
        when(PriceFlashCache.getMinAndMaxFromList(any(), anyInt(), anyInt()))
                .thenReturn(Pair.with(BigDecimal.valueOf(1000), BigDecimal.valueOf(1001)));
        when(futureLogManager.addFutureLog(any())).thenReturn(true);
        String histHighLowFlag = " ";
        futureMonitorService.triggerPriceFlash(futureLiveDO, histHighLowFlag);
    }
}
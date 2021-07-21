package com.guru.future.biz.service;

import com.guru.future.biz.manager.FutureBasicManager;
import com.guru.future.biz.manager.FutureLiveManager;
import com.guru.future.common.entity.converter.ContractRealtimeConverter;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.domain.FutureBasicDO;
import com.guru.future.domain.FutureLiveDO;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * @author j
 */
@Service
public class FutureLiveService {
    @Resource
    private FutureBasicManager futureBasicManager;
    @Resource
    private FutureLiveManager futureLiveManager;

    @Async
    public void refreshLiveData(List<ContractRealtimeDTO> contractRealtimeDTOList) {
        // not in trade time

        Map<String, FutureBasicDO> basicMap = futureBasicManager.getBasicMap();
        for (ContractRealtimeDTO contractRealtimeDTO : contractRealtimeDTOList) {
            FutureLiveDO futureLiveDO = ContractRealtimeConverter.convert2LiveDO(contractRealtimeDTO);
            FutureBasicDO futureBasicDO = basicMap.get(futureLiveDO.getCode());
            BigDecimal histHigh = ObjectUtils.defaultIfNull(futureBasicDO.getHigh(), BigDecimal.ZERO);
            BigDecimal histLow = ObjectUtils.defaultIfNull(futureBasicDO.getLow(), BigDecimal.ZERO);
            if (histHigh.compareTo(histLow) > 0) {
                BigDecimal lowChange = (futureLiveDO.getPrice().subtract(histLow)).multiply(BigDecimal.valueOf(100))
                        .divide(histLow, 2, RoundingMode.HALF_UP);
                BigDecimal highChange = (futureLiveDO.getPrice().subtract(histHigh)).multiply(BigDecimal.valueOf(100))
                        .divide(histHigh, 2, RoundingMode.HALF_UP);

                futureLiveDO.setHighestChange(highChange);
                futureLiveDO.setLowestChange(lowChange);
                // [7345.0 +40.84%, 13040.0 -20.67%]
                StringBuilder waveStr = new StringBuilder();
                waveStr.append("[").append(histLow).append(" ").append("+").append(lowChange).append("%").append(", ")
                        .append(histHigh).append(" ").append(highChange).append("%]");
                futureLiveDO.setWave(waveStr.toString());
            }
            futureLiveManager.upsertFutureLive(futureLiveDO);
        }
    }
}

package com.guru.future.biz.service;

import com.guru.future.biz.handler.FutureTaskDispatcher;
import com.guru.future.biz.manager.FutureBasicManager;
import com.guru.future.biz.manager.FutureLiveManager;
import com.guru.future.common.cache.LiveDataCache;
import com.guru.future.common.entity.converter.ContractRealtimeConverter;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.entity.vo.FutureLiveVO;
import com.guru.future.common.utils.WaveUtil;
import com.guru.future.domain.FutureBasicDO;
import com.guru.future.domain.FutureLiveDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author j
 */
@Service
@Slf4j
public class FutureLiveService {
    @Resource
    private ScheduledExecutorService scheduledExecutor;
    @Resource
    private FutureBasicManager futureBasicManager;
    @Resource
    private FutureLiveManager futureLiveManager;
    @Resource
    private FutureMonitorService monitorService;

    @Async()
    public void reloadLiveCache(List<ContractRealtimeDTO> contractRealtimeDTOList, Map<String, FutureBasicDO> basicMap) {
        List<FutureLiveVO> futureLiveVOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(contractRealtimeDTOList)) {
            return;
        }
        for (ContractRealtimeDTO contractRealtimeDTO : contractRealtimeDTOList) {
            String code = contractRealtimeDTO.getCode();
            FutureBasicDO basicDO = basicMap.get(code);
            FutureLiveDO futureLiveDO = ContractRealtimeConverter.convert2LiveDO(contractRealtimeDTO);
            FutureLiveVO futureLiveVO = new FutureLiveVO();
            futureLiveVO.setWave(WaveUtil.generateWave(basicDO.getA(), basicDO.getB(), basicDO.getC(), futureLiveVO.getPrice()));
            BeanUtils.copyProperties(futureLiveDO, futureLiveVO);
            futureLiveVOList.add(futureLiveVO);
        }
        Collections.sort(futureLiveVOList);
        int size = futureLiveVOList.size();
        int topN = size > 10 ? 10 : size;
        List<FutureLiveVO> lowTop10List = futureLiveVOList.subList(0, topN);
        List<FutureLiveVO> highTop10List = futureLiveVOList.subList(size - topN, size);
        Collections.reverse(highTop10List);
        LiveDataCache.setHighTop10(highTop10List);
        LiveDataCache.setLowTop10(lowTop10List);
    }

    public void refreshLiveData(List<ContractRealtimeDTO> contractRealtimeDTOList, Boolean refresh) {
        Map<String, FutureBasicDO> basicMap = futureBasicManager.getBasicMap(refresh);
        reloadLiveCache(contractRealtimeDTOList, basicMap);
        for (ContractRealtimeDTO contractRealtimeDTO : contractRealtimeDTOList) {
            FutureLiveDO futureLiveDO = ContractRealtimeConverter.convert2LiveDO(contractRealtimeDTO);
            FutureBasicDO futureBasicDO = basicMap.get(futureLiveDO.getCode());

            Pair<BigDecimal, BigDecimal> highLow = updateHistHighLow(contractRealtimeDTO, futureBasicDO);
            BigDecimal histHigh = highLow.getLeft();
            BigDecimal histLow = highLow.getRight();

            String histHighLowFlag = "";
            if (futureLiveDO.getHigh().compareTo(histHigh) >= 0) {
                histHighLowFlag = "^";
            } else if (futureLiveDO.getLow().compareTo(histLow) <= 0) {
                histHighLowFlag = "_";
            }
            monitorService.monitorPriceFlash(futureLiveDO, histHighLowFlag);
            monitorService.addPositionLog(futureLiveDO);
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

    private Pair<BigDecimal, BigDecimal> updateHistHighLow(ContractRealtimeDTO contractRealtimeDTO, FutureBasicDO futureBasicDO) {
        BigDecimal histHigh = ObjectUtils.defaultIfNull(futureBasicDO.getHigh(), BigDecimal.ZERO);
        BigDecimal histLow = ObjectUtils.defaultIfNull(futureBasicDO.getLow(), BigDecimal.ZERO);
        if (contractRealtimeDTO.getLow().compareTo(histLow) < 0) {
            FutureBasicDO updateBasicDO = new FutureBasicDO();
            updateBasicDO.setCode(contractRealtimeDTO.getCode());
            updateBasicDO.setLow(contractRealtimeDTO.getLow());
            updateBasicDO.setRemark("合同新低");
            futureBasicManager.updateBasic(updateBasicDO);
            FutureTaskDispatcher.setRefresh();
            log.info("{} update hist low, refresh basic data", contractRealtimeDTO.getCode());
        }
        if (contractRealtimeDTO.getHigh().compareTo(histHigh) > 0) {
            FutureBasicDO updateBasicDO = new FutureBasicDO();
            updateBasicDO.setCode(contractRealtimeDTO.getCode());
            updateBasicDO.setHigh(contractRealtimeDTO.getHigh());
            updateBasicDO.setRemark("合同新高");
            futureBasicManager.updateBasic(updateBasicDO);
            FutureTaskDispatcher.setRefresh();
            log.info("{} update hist high, refresh basic data", contractRealtimeDTO.getCode());
        }
        return Pair.of(histHigh, histLow);
    }
}

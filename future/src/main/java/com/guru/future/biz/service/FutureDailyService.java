package com.guru.future.biz.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.guru.future.biz.manager.FutureBasicManager;
import com.guru.future.biz.manager.FutureCollectManager;
import com.guru.future.biz.manager.FutureDailyManager;
import com.guru.future.biz.manager.remote.FutureSinaManager;
import com.guru.future.common.entity.converter.ContractRealtimeConverter;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.entity.vo.PositionCollectVO;
import com.guru.future.common.utils.DateUtil;
import com.guru.future.domain.FutureBasicDO;
import com.guru.future.domain.FutureCollectDO;
import com.guru.future.domain.FutureDailyDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author j
 */
@Service
@Slf4j
public class FutureDailyService {
    @Resource
    private FutureBasicManager futureBasicManager;
    @Resource
    private FutureDailyManager futureDailyManager;
    @Resource
    private FutureSinaManager futureSinaManager;
    @Resource
    private FutureCollectManager futureCollectManager;

    @Async
    public void addTradeDaily() {
        List<ContractRealtimeDTO> contractRealtimeDTOList = futureSinaManager.getAllRealtimeFromSina();
        this.upsertTradeDaily(contractRealtimeDTOList);
    }

    @Async
    public void upsertTradeDaily(List<ContractRealtimeDTO> contractRealtimeDTOList) {
        Map<String, FutureBasicDO> basicMap = futureBasicManager.getBasicMap();
        String tradeDate = DateUtil.currentTradeDate();
        Map<String, FutureDailyDO> lastDailyMap = futureDailyManager.getFutureDailyMap(DateUtil.getLastTradeDate(tradeDate), new ArrayList<>(basicMap.keySet()));
        Map<String, FutureDailyDO> existedDailyMap = futureDailyManager.getFutureDailyMap(tradeDate, new ArrayList<>(basicMap.keySet()));
        for (ContractRealtimeDTO contractRealtimeDTO : contractRealtimeDTOList) {
            FutureDailyDO currentDailyDO = ContractRealtimeConverter.convert2DailyDO(contractRealtimeDTO);
            FutureDailyDO existedDailyDO = existedDailyMap.get(currentDailyDO.getCode());
            if (existedDailyDO != null) {
                log.info("update log >>> currentDailyDO={}, existedDailyDO={}", currentDailyDO, existedDailyDO);
                if (!currentDailyDO.changFlag().equals(existedDailyDO.changFlag())) {
                    currentDailyDO.setTradeDate(tradeDate);
                    currentDailyDO.setRemark("update current date");
                    futureDailyManager.updateFutureDaily(currentDailyDO);
                }
                if (DateUtil.dayClose()) {
                    this.addNextTradeDaily(contractRealtimeDTO.getTradeDate(), currentDailyDO);
                }
            } else {
                FutureDailyDO lastDailyDO = lastDailyMap.get(currentDailyDO.getCode());
                if (lastDailyDO != null && currentDailyDO.getTradeDate().compareTo(lastDailyDO.getTradeDate()) > 0) {
                    currentDailyDO.setPreClose(lastDailyDO.getClose());
                    currentDailyDO.setPreSettle(lastDailyDO.getSettle());
                }
                futureDailyManager.addFutureDaily(currentDailyDO);
                this.addNextTradeDaily(contractRealtimeDTO.getTradeDate(), currentDailyDO);
            }
        }
    }

    private void addNextTradeDaily(String tradeDate, FutureDailyDO currentDailyDO) {
        FutureDailyDO nextDailyDO = new FutureDailyDO(currentDailyDO.getSymbol(),
                DateUtil.getNextTradeDate(tradeDate),
                currentDailyDO.getCode(), currentDailyDO.getName(), currentDailyDO.getClose());
        nextDailyDO.setClose(currentDailyDO.getClose());
        nextDailyDO.setOpen(currentDailyDO.getOpen());
        nextDailyDO.setHigh(currentDailyDO.getHigh());
        nextDailyDO.setLow(currentDailyDO.getLow());
        nextDailyDO.setPreSettle(currentDailyDO.getSettle());
        nextDailyDO.setRemark("init next daily");
        try {
            FutureDailyDO existDaily = futureDailyManager.getFutureDaily(nextDailyDO.getTradeDate(), nextDailyDO.getCode());
            if (existDaily == null) {
                futureDailyManager.addFutureDaily(nextDailyDO);
            }
        } catch (Exception e) {
            log.error("futureDailyManager.addFutureDaily failed! error:", e);
        }
    }

    public PositionCollectVO getCurrentPositionList(String tradeDate, List<String> codes) {
        if (Strings.isNullOrEmpty(tradeDate)) {
            tradeDate = DateUtil.latestTradeDate();
        }
        if (CollectionUtils.isEmpty(codes)) {
            codes = Lists.newArrayList("AP2205", "CJ2205", "CF2205", "RU2205", "SA2205", "NI2202", "P2205", "EG2205", "UR2205", "J2205", "FU2205", "SF2205", "I2205", "RB2205");
        }
        List<FutureCollectDO> futureCollectDOList = futureCollectManager.getDailyCollect(tradeDate, codes);
        Map<String, List<FutureCollectDO>> collectMap = futureCollectDOList.stream()
                .collect(Collectors.groupingBy(e -> DateUtil.toHourMinute(e.getCreateTime())));
        Map<Integer, Integer> countMap = new HashMap<>();
        Map<Integer, Set<String>> sizeTimeMap = new HashMap<>();
        for (Map.Entry<String, List<FutureCollectDO>> entry : collectMap.entrySet()) {
            Integer size = entry.getValue().size();
            Set<String> sizeTimeList = ObjectUtils.defaultIfNull(sizeTimeMap.get(size), new HashSet<>());
            List<String> times = entry.getValue().stream().map(e -> DateUtil.toHourMinute(e.getCreateTime())).collect(Collectors.toList());
            sizeTimeList.addAll(times);
            sizeTimeMap.put(size, sizeTimeList);
            countMap.put(size, ObjectUtils.defaultIfNull(countMap.get(size), 0) + 1);
        }
        int mostNumber = 0;
        int mostSize = Collections.max(countMap.values());
        for (Map.Entry<Integer, Integer> entry : countMap.entrySet()) {
            //循环遍历，在HashMap中找到vmaxCount（最大重复次数）所对应的key（最大重复次数所对应的数值）
            if (entry.getValue() == mostSize) {
                mostNumber = entry.getKey();
                break;
            }
        }
        Set<String> mostTimes = sizeTimeMap.get(mostNumber);
        Map<String, List<FutureCollectDO>> nameCollectMap = futureCollectDOList.stream()
                .collect(Collectors.groupingBy(FutureCollectDO::getName));
        List<PositionCollectVO.Position> positionDataList = new ArrayList<>();
        List<String> timeList = new ArrayList<>();
        for (Map.Entry<String, List<FutureCollectDO>> entry : nameCollectMap.entrySet()) {
            String name = entry.getKey();
            List<Integer> positions = new ArrayList<>();
            for (FutureCollectDO collectDO : entry.getValue()) {
                String hourMinuteTime = DateUtil.toHourMinute(collectDO.getCreateTime());
                if (!mostTimes.contains(hourMinuteTime)) {
                    log.warn("不包含的时间={}, 跳过", hourMinuteTime);
                    continue;
                }
                positions.add(collectDO.getPosition());
                if (timeList.size() < mostSize) {
                    timeList.add(hourMinuteTime);
                }
            }
            PositionCollectVO.Position position = new PositionCollectVO.Position();
            position.setName(name);
            position.setData(positions);
            positionDataList.add(position);
        }

        PositionCollectVO positionCollectVO = new PositionCollectVO();
        positionCollectVO.setTimeList(timeList);
        positionCollectVO.setPositionData(positionDataList);
        return positionCollectVO;
    }
}

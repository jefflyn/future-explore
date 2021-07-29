package com.guru.future.biz.service;

import com.guru.future.biz.manager.FutureBasicManager;
import com.guru.future.biz.manager.FutureDailyManager;
import com.guru.future.biz.manager.FutureMailManager;
import com.guru.future.biz.manager.FutureSinaManager;
import com.guru.future.common.entity.dto.ContractOpenGapDTO;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.utils.DateUtil;
import com.guru.future.domain.FutureBasicDO;
import com.guru.future.domain.FutureDailyDO;
import lombok.extern.slf4j.Slf4j;
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

/**
 * @author j
 */
@Service
@Slf4j
public class FutureGapService {
    @Resource
    private FutureBasicManager futureBasicManager;
    @Resource
    private FutureDailyManager futureDailyManager;
    @Resource
    private FutureSinaManager futureSinaManager;
    @Resource
    private FutureMailManager futureMailManager;

    @Async
    public void monitorOpenGap() {
        List<ContractRealtimeDTO> contractRealtimeDTOList = futureSinaManager.getAllRealtimeFromSina();
        this.noticeOpenGap(contractRealtimeDTOList);
    }

    public void noticeOpenGap(List<ContractRealtimeDTO> contractRealtimeDTOList) {
        Map<String, FutureBasicDO> basicMap = futureBasicManager.getBasicMap();
        String tradeDate = DateUtil.currentDate();
        Map<String, FutureDailyDO> lastDailyMap = futureDailyManager.getFutureDailyMap(tradeDate, new ArrayList<>(basicMap.keySet()));

        List<ContractOpenGapDTO> openGapDTOList = new ArrayList<>();
        for (ContractRealtimeDTO realtimeDTO : contractRealtimeDTOList) {
            String code = realtimeDTO.getCode();
            FutureBasicDO basicDO = basicMap.get(code);
            int nightTrade = basicDO.getNight();
            FutureDailyDO lastDailyDO = lastDailyMap.get(realtimeDTO.getCode());
            BigDecimal lastClose = lastDailyDO.getClose();
            BigDecimal currentOpen = realtimeDTO.getOpen();

            if (DateUtil.isNight()) {
                if (nightTrade == 0) {
                    continue;
                }
            } else {
                if (nightTrade == 1) {
                    continue;
                }
                lastClose = lastDailyDO.getPreClose();
            }
            BigDecimal priceDiff = currentOpen.subtract(lastClose);
            BigDecimal gapRate = priceDiff.multiply(BigDecimal.valueOf(100)).divide(lastClose, 2, RoundingMode.HALF_UP);

            if (Math.abs(gapRate.floatValue()) >= 0.5) {
                ContractOpenGapDTO openGapDTO = new ContractOpenGapDTO();
                openGapDTO.setCode(code);
                openGapDTO.setName(basicDO.getName());
                openGapDTO.setCategory(basicDO.getType());
                openGapDTO.setPreClose(lastClose);
                openGapDTO.setOpen(currentOpen);
                openGapDTO.setGapRate(gapRate);
                String remark = "跳空" + gapRate + "%, 价差" + priceDiff;
                openGapDTO.setRemark(remark);
                openGapDTOList.add(openGapDTO);
            }
        }
        if (!CollectionUtils.isEmpty(openGapDTOList)) {
            log.info("{}", openGapDTOList);
//            DataFrame<ContractOpenGapDTO> df = new DataFrame<>("category", "code", "name", "preClose", "open", "gapRate", "remark");
//            df.append(openGapDTOList);
            StringBuilder openGapStr = new StringBuilder();
            Collections.sort(openGapDTOList);
            Collections.reverse(openGapDTOList);
            for (ContractOpenGapDTO openGapDTO : openGapDTOList) {
                openGapStr.append(openGapDTO.getCategory()).append(",")
                        .append(openGapDTO.getCode()).append(",")
                        .append(openGapDTO.getName()).append(",")
//                        .append(openGapDTO.getPreClose()).append(",")
//                        .append(openGapDTO.getOpen()).append(",")
                        .append(openGapDTO.getGapRate()).append(",")
                        .append(openGapDTO.getRemark()).append("\n");
            }
            futureMailManager.notifyOpenGap(openGapStr.toString());
        }
    }
}

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
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
        try {
            this.noticeOpenGap(contractRealtimeDTOList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void noticeOpenGap(List<ContractRealtimeDTO> contractRealtimeDTOList) throws Exception {
        Map<String, FutureBasicDO> basicMap = futureBasicManager.getBasicMap();
        String tradeDate = DateUtil.currentTradeDate();
        if (DateUtil.isNight()) {
            tradeDate = DateUtil.getNextTradeDate(tradeDate);
        }
        Map<String, FutureDailyDO> preDailyMap = futureDailyManager.getFutureDailyMap(tradeDate, new ArrayList<>(basicMap.keySet()));
        List<ContractOpenGapDTO> openGapDTOList = new ArrayList<>();
        for (ContractRealtimeDTO realtimeDTO : contractRealtimeDTOList) {
            log.info("realtimeDTO={}", realtimeDTO);
            String code = realtimeDTO.getCode();
            FutureBasicDO basicDO = basicMap.get(code);
            int nightTrade = basicDO.getNight();
            if (DateUtil.isNight() && nightTrade == 0) {
                continue;
            }
            FutureDailyDO lastDailyDO = preDailyMap.get(realtimeDTO.getCode());
            if (lastDailyDO == null) {
                continue;
            }
            log.info("lastDailyDO={}", lastDailyDO);
            BigDecimal preClose = lastDailyDO.getPreClose();
            BigDecimal preOpen = lastDailyDO.getOpen();
            BigDecimal preHigh = lastDailyDO.getHigh();
            BigDecimal preLow = lastDailyDO.getLow();
            BigDecimal currentOpen = realtimeDTO.getOpen();
            if (currentOpen.compareTo(preOpen) == 0) {
                log.warn("{}-{}昨开等于今开{}!!!", realtimeDTO.getName(), code, currentOpen);
                continue;
            }
            BigDecimal priceDiff = currentOpen.subtract(preClose);
            BigDecimal gapRate = priceDiff.multiply(BigDecimal.valueOf(100)).divide(preClose, 2, RoundingMode.HALF_UP);
            if (Math.abs(gapRate.floatValue()) >= 0.5) {
                BigDecimal dayGap = null;
                BigDecimal suggestFrom = null;
                BigDecimal suggestTo = null;
                if (priceDiff.compareTo(BigDecimal.ZERO) >= 0) {
                    // 日级别跳空高开
                    if (currentOpen.compareTo(preHigh) > 0) {
                        dayGap = (currentOpen.subtract(preHigh)).multiply(BigDecimal.valueOf(100)).divide(preHigh, 2, RoundingMode.HALF_UP);
                        suggestFrom = preHigh;
                        suggestTo = currentOpen.multiply(BigDecimal.valueOf(1.03F));
                    } else {
                        suggestFrom = preClose;
                        suggestTo = currentOpen.multiply(BigDecimal.valueOf(1.05F));
                    }
                } else {
                    // 日级别跳空低开
                    if (currentOpen.compareTo(preLow) < 0) {
                        dayGap = (currentOpen.subtract(preLow)).multiply(BigDecimal.valueOf(100)).divide(preLow, 2, RoundingMode.HALF_UP);
                        suggestFrom = currentOpen.multiply(BigDecimal.valueOf(0.97F));
                        suggestTo = preLow.multiply(BigDecimal.valueOf(1.03F));
                    } else {
                        suggestFrom = currentOpen.multiply(BigDecimal.valueOf(0.99F));
                        suggestTo = preClose.multiply(BigDecimal.valueOf(1.05F));
                    }
                }
                suggestFrom = suggestFrom.setScale(1, RoundingMode.HALF_UP);
                suggestTo = suggestTo.setScale(1, RoundingMode.HALF_UP);
                String suggestPrice = suggestFrom + "-" + suggestTo;
                ContractOpenGapDTO openGapDTO = new ContractOpenGapDTO();
                openGapDTO.setCode(code);
                openGapDTO.setName(basicDO.getName());
                openGapDTO.setCategory(basicDO.getType());
                openGapDTO.setPreClose(preClose.setScale(1, RoundingMode.HALF_UP));
                openGapDTO.setOpen(currentOpen);
                openGapDTO.setGapRate(gapRate);
                String remark = "日跳空 " + dayGap + "%";
                openGapDTO.setRemark(dayGap != null ? remark : "");
                openGapDTO.setSuggest(suggestPrice);
                openGapDTOList.add(openGapDTO);
            }
        }
        if (!CollectionUtils.isEmpty(openGapDTOList)) {
//            DataFrame<ContractOpenGapDTO> df = new DataFrame<>("category", "code", "name", "preClose", "open", "gapRate", "remark");
//            df.append(openGapDTOList);
            Collections.sort(openGapDTOList);
            Collections.reverse(openGapDTOList);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\r\n");
            stringBuilder.append("<html><head></head><body><h3>");
            stringBuilder.append(DateFormatUtils.format(new Date(), DateUtil.COMMON_DATE_PATTERN)).append("</h3>");
            stringBuilder.append("<table cellspacing=\"0\" cellpadding=\"1\" border=\"1\" style=\"border:solid 1px #E8F2F9;font-size=14px;;font-size:12px;\">");
            stringBuilder.append("<tr style=\"background-color: #428BCA; color:#ffffff\">" +
                    "<th width=\"60px\">品种</th>" +
                    "<th width=\"60px\">编码</th>" +
                    "<th width=\"80px\">名称</th>" +
                    "<th width=\"60px\">昨收</th>" +
                    "<th width=\"60px\">今开</th>" +
                    "<th width=\"60px\">缺口</th>" +
                    "<th width=\"80px\">备注</th>" +
                    "<th width=\"120px\">建议</th>" +
                    "</tr>");
            for (ContractOpenGapDTO openGapDTO : openGapDTOList) {
                stringBuilder.append("</tr>");
                stringBuilder.append("<td style=\"text-align:left\">" + openGapDTO.getCategory() + "</td>");
                stringBuilder.append("<td style=\"text-align:left\">" + openGapDTO.getCode() + "</td>");
                stringBuilder.append("<td style=\"text-align:center\">" + openGapDTO.getName() + "</td>");
                stringBuilder.append("<td style=\"text-align:right\">" + openGapDTO.getPreClose() + "</td>");
                stringBuilder.append("<td style=\"text-align:right\">" + openGapDTO.getOpen() + "</td>");
                stringBuilder.append("<td style=\"text-align:right\">" + openGapDTO.getGapRate() + "%</td>");
                stringBuilder.append("<td style=\"text-align:left\">" + openGapDTO.getRemark() + "</td>");
                stringBuilder.append("<td style=\"text-align:center\">" + openGapDTO.getSuggest() + "</td>");
                stringBuilder.append("</tr>");
            }
            stringBuilder.append("</table>");
            stringBuilder.append("</body></html>");
            futureMailManager.notifyOpenGapHtml(stringBuilder.toString());
        }
    }
}

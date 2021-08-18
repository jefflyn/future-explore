package com.guru.future.biz.service;

import com.guru.future.biz.manager.FutureBasicManager;
import com.guru.future.biz.manager.FutureDailyManager;
import com.guru.future.biz.manager.FutureMailManager;
import com.guru.future.biz.manager.FutureSinaManager;
import com.guru.future.biz.manager.OpenGapManager;
import com.guru.future.common.entity.converter.ContractOpenGapConverter;
import com.guru.future.common.entity.dto.ContractOpenGapDTO;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.utils.DateUtil;
import com.guru.future.domain.FutureBasicDO;
import com.guru.future.domain.FutureDailyDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
import java.util.HashMap;
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
    @Resource
    private OpenGapManager openGapManager;

    @Async
    public void monitorOpenGap() {
        List<ContractRealtimeDTO> contractRealtimeDTOList = futureSinaManager.getAllRealtimeFromSina();
        try {
            noticeOpenGap(contractRealtimeDTOList);
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
        int total = 0;
        Map<String, Integer> openStats = new HashMap<>();
        for (ContractRealtimeDTO realtimeDTO : contractRealtimeDTOList) {
//            log.info("{}: realtimeDTO={}", realtimeDTO.getName(), realtimeDTO);
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
//            log.info("lastDailyDO={}", lastDailyDO);
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
            total += 1;
            if (priceDiff.compareTo(BigDecimal.ZERO) == 0) {
                Integer cnt = ObjectUtils.defaultIfNull(openStats.get("平开"), 0);
                openStats.put("平开", cnt + 1);
            } else if (priceDiff.compareTo(BigDecimal.ZERO) > 0) {
                Integer cnt = ObjectUtils.defaultIfNull(openStats.get("高开"), 0);
                openStats.put("高开", cnt + 1);
            } else {
                Integer cnt = ObjectUtils.defaultIfNull(openStats.get("低开"), 0);
                openStats.put("低开", cnt + 1);
            }
            BigDecimal gapRate = priceDiff.multiply(BigDecimal.valueOf(100)).divide(preClose, 2, RoundingMode.HALF_UP);
            if (Math.abs(gapRate.floatValue()) >= 0.5) {
                BigDecimal dayGap = null;
                BigDecimal suggestFrom = null;
                BigDecimal suggestTo = null;
                StringBuilder remark = new StringBuilder("");
                if (priceDiff.compareTo(BigDecimal.ZERO) >= 0) {
                    suggestFrom = preClose;
                    // 日级别跳空高开
                    if (currentOpen.compareTo(preHigh) > 0) {
                        dayGap = (currentOpen.subtract(preHigh)).multiply(BigDecimal.valueOf(100)).divide(preHigh, 2, RoundingMode.HALF_UP);
                        if (Math.abs(dayGap.floatValue()) >= 1.5) {
                            suggestTo = currentOpen.multiply(BigDecimal.valueOf(1.005));
                        } else {
                            suggestTo = currentOpen.multiply(BigDecimal.valueOf(1 + (1.5 - dayGap.floatValue()) / 100));
                        }
                        remark.append("日跳高 ").append("+").append(dayGap).append("%");
                    } else {
                        if (Math.abs(gapRate.floatValue()) >= 1.5) {
                            suggestTo = currentOpen.multiply(BigDecimal.valueOf(1.003));
                        } else {
                            suggestTo = currentOpen.multiply(BigDecimal.valueOf(1 + (1.5 - gapRate.floatValue()) / 100));
                        }
                    }
                } else {
//                    suggestFrom = currentOpen.multiply(BigDecimal.valueOf(1 - Math.abs(gapRate.floatValue() / 100)));
                    suggestFrom = currentOpen.multiply(BigDecimal.valueOf(0.996));
                    // 日级别跳空低开
                    if (currentOpen.compareTo(preLow) < 0) {
                        dayGap = (currentOpen.subtract(preLow)).multiply(BigDecimal.valueOf(100)).divide(preLow, 2, RoundingMode.HALF_UP);
//                        suggestFrom = currentOpen.multiply(BigDecimal.valueOf(1 - Math.abs(dayGap.floatValue() / 100)));
                        if (Math.abs(dayGap.floatValue()) >= 1.5) {
                            suggestTo = preLow.multiply(BigDecimal.valueOf(1.005));
                        } else {
                            suggestTo = preLow.multiply(BigDecimal.valueOf(1 + (1.5 + dayGap.floatValue()) / 100));
                        }
                        remark.append("日跳空 ").append(dayGap).append("%");
                    } else {
                        if (Math.abs(gapRate.floatValue()) >= 1.5) {
                            suggestTo = preClose.multiply(BigDecimal.valueOf(1.003));
                        } else {
                            suggestTo = preClose.multiply(BigDecimal.valueOf(1 + (1.5 + gapRate.floatValue()) / 100));
                        }
                    }
                }
                suggestFrom = suggestFrom.setScale(1, RoundingMode.HALF_UP);
                suggestTo = suggestTo.setScale(1, RoundingMode.HALF_UP);
                String suggestPrice = suggestFrom + "-" + suggestTo;
                ContractOpenGapDTO openGapDTO = ContractOpenGapDTO.builder()
                        .tradeDate(tradeDate).code(code).name(basicDO.getName()).category(basicDO.getType())
                        .preClose(preClose.setScale(1, RoundingMode.HALF_UP)).open(currentOpen).gapRate(gapRate)
                        .remark(remark.toString()).buyLow(suggestFrom).sellHigh(suggestTo).suggest(suggestPrice).build();
                openGapDTOList.add(openGapDTO);
            }
        }
        // 2021-08-18 高开:20 低开:10 平开:1
        StringBuilder title = new StringBuilder(tradeDate + "<br/>");
        for (String openKey : openStats.keySet()) {
            int count = openStats.get(openKey);
            float rate = count * 100 / (float) total;
            String openStr = openKey + ":" + count + "," + String.format("%.1f", rate) + "%";
            title.append(" ").append(openStr);
        }
        if (!CollectionUtils.isEmpty(openGapDTOList)) {
            sendOpenGapMail(title.toString(), openGapDTOList);
            openGapManager.batchAddOpenGapLog(ContractOpenGapConverter.convert2OpenGapDOList(openGapDTOList));
        } else {
            log.info(">>> no specified open gap found!!!");
        }
    }

    @Async
    public void sendOpenGapMail(String title, List<ContractOpenGapDTO> openGapDTOList) throws Exception {
        Collections.sort(openGapDTOList);
        Collections.reverse(openGapDTOList);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\r\n");
        stringBuilder.append("<html><head></head><body><h3>");
        stringBuilder.append(title).append("</h3>");
        stringBuilder.append("<table cellspacing=\"0\" cellpadding=\"1\" border=\"1\" style=\"border:solid 1px #E8F2F9;font-size=14px;;font-size:12px;\">");
        stringBuilder.append("<tr style=\"background-color: #428BCA; color:#ffffff\">" +
                "<th width=\"10px\">序号</th>" +
                "<th width=\"60px\">品种</th>" +
                "<th width=\"60px\">编码</th>" +
                "<th width=\"80px\">名称</th>" +
                "<th width=\"60px\">昨收</th>" +
                "<th width=\"60px\">今开</th>" +
                "<th width=\"60px\">缺口</th>" +
                "<th width=\"80px\">备注</th>" +
                "<th width=\"120px\">建议</th>" +
                "</tr>");
        int seq = 0;
        for (ContractOpenGapDTO openGapDTO : openGapDTOList) {
            stringBuilder.append("</tr>");
            stringBuilder.append("<td style=\"text-align:left\">" + (++seq) + "</td>");
            stringBuilder.append("<td style=\"text-align:center\">" + openGapDTO.getCategory() + "</td>");
            stringBuilder.append("<td style=\"text-align:center\">" + openGapDTO.getCode() + "</td>");
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

package com.guru.future.biz.service;

import com.guru.future.biz.manager.FutureBasicManager;
import com.guru.future.biz.manager.FutureDailyManager;
import com.guru.future.biz.manager.FutureMailManager;
import com.guru.future.biz.manager.OpenGapManager;
import com.guru.future.biz.manager.remote.FutureSinaManager;
import com.guru.future.common.entity.converter.ContractOpenGapConverter;
import com.guru.future.common.entity.dto.ContractOpenGapDTO;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.utils.DateUtil;
import com.guru.future.common.utils.FutureUtil;
import com.guru.future.common.utils.NumberUtil;
import com.guru.future.domain.FutureBasicDO;
import com.guru.future.domain.FutureDailyDO;
import com.guru.future.domain.OpenGapDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import static com.guru.future.common.utils.DateUtil.TRADE_DATE_PATTERN_FLAT;
import static com.guru.future.common.utils.FutureUtil.PERCENTAGE_SYMBOL;

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

    @Cacheable(cacheManager = "hour1CacheManager", value = "marketOverview", key = "#currentDate", unless = "#result==null")
    public String getMarketOverview(String currentDate) {
        List<OpenGapDO> openGapDOList = openGapManager.getCurrentOpenGap();
        if (CollectionUtils.isEmpty(openGapDOList)) {
            return null;
        }
        float total = openGapDOList.size();
        int openHighCount = 0;
        int openLowCount = 0;
        int openFlat = 0;
        for (OpenGapDO openGapDO : openGapDOList) {
            if (openGapDO.getGapRate().compareTo(BigDecimal.ZERO) > 0) {
                openHighCount += 1;
            } else if (openGapDO.getGapRate().compareTo(BigDecimal.ZERO) < 0) {
                openLowCount += 1;
            } else {
                openFlat += 1;
            }
        }
        String overview = "中性";
        float highRate = openHighCount * 100 / total;
        float lowRate = openLowCount * 100 / total;
        if (highRate >= 70) {
            overview = "多";
        } else if (highRate >= 55) {
            overview = "偏多";
        } else if (lowRate >= 70) {
            overview = "空";
        } else if (lowRate >= 55) {
            overview = "偏空";
        }
        return overview;
    }

    public void monitorOpenGap() throws InterruptedException {
        LongAdder times = new LongAdder();
        while (DateUtil.beforeBidTime()) {
            log.warn("monitorOpenGap before bid time !!!");
            TimeUnit.SECONDS.sleep(1L);
            times.increment();
            if (times.intValue() > 4) {
                return;
            }
        }
        List<ContractRealtimeDTO> contractRealtimeDTOList = futureSinaManager.getAllRealtimeFromSina();
        try {
            noticeOpenGap(contractRealtimeDTOList);
        } catch (Exception e) {
            log.error("monitor open gap failed, error={}", e);
            e.printStackTrace();
        }
    }

    public void noticeOpenGap(List<ContractRealtimeDTO> contractRealtimeDTOList) throws Exception {
        log.info("open gap start! realtime data size={}", contractRealtimeDTOList.size());
        Map<String, FutureBasicDO> basicMap = futureBasicManager.getBasicMap();
        String tradeDate = DateUtil.latestTradeDate(TRADE_DATE_PATTERN_FLAT);
        List<String> tsCodes = futureBasicManager.getAllTsCodes();
        Map<String, FutureDailyDO> preDailyMap = futureDailyManager.getFutureDailyMap(tradeDate, tsCodes);
        List<ContractOpenGapDTO> openGapDTOList = new ArrayList<>();
        int total = 0;
        Map<String, Integer> openStats = new HashMap<>();
        String openHighTag = "高开";
        String openLowTag = "低开";
        for (ContractRealtimeDTO realtimeDTO : contractRealtimeDTOList) {
//            log.info("{}: realtimeDTO={}", realtimeDTO.getName(), realtimeDTO);
            String code = realtimeDTO.getCode();
            FutureBasicDO basicDO = basicMap.get(code);
            int nightTrade = basicDO.getNight();
            if (DateUtil.isNight() && nightTrade == 0) {
                log.warn("{} night trade not support", code);
                continue;
            }
            FutureDailyDO lastDailyDO = preDailyMap.get(realtimeDTO.getCode());
            if (lastDailyDO == null) {
                log.warn("last daily data missed! trade date:{}", tradeDate);
                continue;
            }
//            log.info("lastDailyDO={}", lastDailyDO);
            BigDecimal preClose = lastDailyDO.getClose();
            BigDecimal preOpen = lastDailyDO.getOpen();
            BigDecimal preHigh = lastDailyDO.getHigh();
            BigDecimal preLow = lastDailyDO.getLow();
            BigDecimal currentOpen = realtimeDTO.getOpen();
            if (currentOpen.compareTo(preOpen) == 0) {
                log.warn("{} 昨开等于今开 {}!!!", realtimeDTO.getName(), currentOpen);
                continue;
            }
            BigDecimal priceDiff = currentOpen.subtract(preClose);
            BigDecimal openChange = priceDiff.multiply(BigDecimal.valueOf(100)).divide(preClose, 2, RoundingMode.HALF_UP);
            total += 1;
            boolean isUp = priceDiff.compareTo(BigDecimal.ZERO) > 0;
            if (priceDiff.compareTo(BigDecimal.ZERO) == 0) {
//                Integer cnt = ObjectUtils.defaultIfNull(openStats.get("平开"), 0);
//                openStats.put("平开", cnt + 1);
            } else if (isUp) {
                Integer cnt = ObjectUtils.defaultIfNull(openStats.get("高开"), 0);
                openStats.put(openHighTag, cnt + 1);
            } else {
                Integer cnt = ObjectUtils.defaultIfNull(openStats.get("低开"), 0);
                openStats.put(openLowTag, cnt + 1);
            }
            //if (Math.abs(gapRate.floatValue()) >= 0.5) {
            BigDecimal dayGap = BigDecimal.ZERO;
            BigDecimal suggestFrom;
            BigDecimal suggestTo;
            StringBuilder remark = new StringBuilder("");
            boolean isDayGap = false;
            if (priceDiff.compareTo(BigDecimal.ZERO) >= 0) {
                // 日级别高开
                if (currentOpen.compareTo(preHigh) > 0) {
                    isDayGap = true;
                    suggestFrom = preClose.multiply(BigDecimal.valueOf(0.996));
                    dayGap = (currentOpen.subtract(preHigh)).multiply(BigDecimal.valueOf(100)).divide(preHigh, 2, RoundingMode.HALF_UP);
                    remark.append("日缺口 ").append("+").append(dayGap).append("%");
                } else {
                    suggestFrom = preClose.multiply(BigDecimal.valueOf(0.999));
                    remark.append("高开 ").append("+").append(openChange).append("%");
                }
                if (Math.abs(openChange.floatValue()) >= 0.6) {
                    suggestFrom = currentOpen.multiply(BigDecimal.valueOf(0.9964));
                }
                if (Math.abs(openChange.floatValue()) >= 5) {
                    suggestTo = BigDecimal.valueOf(999999);
                } else if (Math.abs(openChange.floatValue()) >= 2) {
                    BigDecimal factor = BigDecimal.ONE.add(openChange.divide(BigDecimal.valueOf(100)));
                    suggestTo = currentOpen.multiply(factor);
                } else if (Math.abs(openChange.floatValue()) >= 1) {
                    suggestTo = currentOpen.multiply(BigDecimal.valueOf(1.0075));
                } else {
                    suggestTo = currentOpen.multiply(BigDecimal.valueOf(1.007));
                }
            } else {
                // 日级别低开
                if (currentOpen.compareTo(preLow) < 0) {
                    isDayGap = true;
                    suggestFrom = currentOpen.multiply(BigDecimal.valueOf(0.996));
                    dayGap = (currentOpen.subtract(preLow)).multiply(BigDecimal.valueOf(100)).divide(preLow, 2, RoundingMode.HALF_UP);
                    remark.append("日缺口 ").append(dayGap).append("%");
                } else {
                    suggestFrom = currentOpen.multiply(BigDecimal.valueOf(0.999));
                    remark.append("低开 ").append(openChange).append("%");
                }
                suggestTo = currentOpen.multiply(BigDecimal.valueOf(1.007));
            }
            suggestFrom = suggestFrom.setScale(1, RoundingMode.HALF_UP);
            suggestTo = suggestTo.setScale(1, RoundingMode.HALF_UP);
            String suggestPrice = suggestFrom.intValue() + "多\n"
                    + suggestTo.intValue() + "空";
            int calcPosition = FutureUtil.calcPosition(isUp, realtimeDTO.getOpen(), basicDO.getHigh(), basicDO.getLow());
            ContractOpenGapDTO openGapDTO = ContractOpenGapDTO.builder()
                    .tradeDate(tradeDate).code(code).name(basicDO.getName()).category(basicDO.getType()).dayGap(isDayGap)
                    .preClose(preClose.setScale(1, RoundingMode.HALF_UP))
                    .preHigh(preHigh).preLow(preLow).open(currentOpen).openChange(openChange).gapRate(dayGap).contractPosition(calcPosition)
                    .remark(remark.toString()).buyLow(suggestFrom).sellHigh(suggestTo).suggest(suggestPrice).build();
            openGapDTOList.add(openGapDTO);
        }

        if (total == 0) {
            log.warn("total == 0, no gap data");
            return;
        }

        String overview = "中性";
        float highRate = ObjectUtils.defaultIfNull(openStats.get(openHighTag), 0) * 100 / (float) total;
        float lowRate = ObjectUtils.defaultIfNull(openStats.get(openLowTag), 0) * 100 / (float) total;
        if (highRate >= 70) {
            overview = "多";
        } else if (highRate >= 55) {
            overview = "偏多";
        } else if (lowRate >= 70) {
            overview = "空";
        } else if (lowRate >= 55) {
            overview = "偏空";
        }
        String openHighStr = openHighTag + ":" + String.format("%.1f", highRate) + "%";
        String openLowStr = openLowTag + ":" + String.format("%.1f", lowRate) + "%";

        // 2021-08-18 高开:20 低开:10 平开:1
        StringBuilder title = new StringBuilder(tradeDate + "【" + overview + "】" + "<br/>");
        title.append(openHighStr).append(" VS ").append(openLowStr);

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
                "<th width=\"20px\">序号</th>" +
//                "<th width=\"60px\">品种</th>" +
                "<th width=\"60px\">编码</th>" +
                "<th width=\"80px\">名称</th>" +
                "<th width=\"60px\">昨收</th>" +
                "<th width=\"60px\">今开</th>" +
                "<th width=\"100px\">备注</th>" +
                "<th width=\"60px\">pos</th>" +
                "<th width=\"140px\">建议</th>" +
                "</tr>");
        int seq = 0;
        for (ContractOpenGapDTO openGapDTO : openGapDTOList) {
            if (Math.abs(openGapDTO.getOpenChange().floatValue()) >= 0.33 || openGapDTO.getDayGap()) {
                String priceTag = openGapDTO.getGapRate().floatValue() > 0 ?
                        "^" + NumberUtil.price2String(openGapDTO.getPreHigh()) + "\n" + NumberUtil.price2String(openGapDTO.getPreClose()) :
                        NumberUtil.price2String(openGapDTO.getPreClose()) + "\n" + "_" + NumberUtil.price2String(openGapDTO.getPreLow());
                String trHtml = "<tr>";
                if (openGapDTO.getDayGap() && openGapDTO.getGapRate().floatValue() > 0) {
                    trHtml = "<tr style=\"color:red\">";
                }
                if (openGapDTO.getDayGap() && openGapDTO.getGapRate().floatValue() < 0) {
                    trHtml = "<tr style=\"color:green\">";
                }
                stringBuilder.append(trHtml);
                stringBuilder.append("<td style=\"text-align:right\">" + (++seq) + "</td>");
//                stringBuilder.append("<td style=\"text-align:center\">" + openGapDTO.getCategory() + "</td>");
                stringBuilder.append("<td style=\"text-align:center\">" + openGapDTO.getCode() + "</td>");
                stringBuilder.append("<td style=\"text-align:center\">" + openGapDTO.getName() + "</td>");
                stringBuilder.append("<td style=\"text-align:right\">" + priceTag + "</td>");
                stringBuilder.append("<td style=\"text-align:right\">" + NumberUtil.price2String(openGapDTO.getOpen())
                        + "\n" + openGapDTO.getOpenChange() + PERCENTAGE_SYMBOL + "</td>");
                stringBuilder.append("<td style=\"text-align:center\">" + openGapDTO.getRemark() + "</td>");
                stringBuilder.append("<td style=\"text-align:center\">" + openGapDTO.getContractPosition() + "%" + "</td>");
                stringBuilder.append("<td style=\"text-align:center\">" + openGapDTO.getSuggest() + "</td>");
                stringBuilder.append("</tr>");
            }
        }
        stringBuilder.append("</table>");
        stringBuilder.append("</body></html>");
        futureMailManager.sendHtmlMail("缺口报告", stringBuilder.toString());
    }
}

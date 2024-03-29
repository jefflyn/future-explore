package com.guru.future.biz.service;

import com.guru.future.biz.manager.BasicManager;
import com.guru.future.biz.manager.ContractManager;
import com.guru.future.biz.manager.FutureDailyManager;
import com.guru.future.biz.manager.FutureMailManager;
import com.guru.future.biz.manager.OpenGapManager;
import com.guru.future.biz.manager.remote.FutureSinaManager;
import com.guru.future.common.entity.converter.ContractOpenGapConverter;
import com.guru.future.common.entity.dao.OpenGapDO;
import com.guru.future.common.entity.dao.TradeDailyDO;
import com.guru.future.common.entity.domain.Basic;
import com.guru.future.common.entity.domain.Contract;
import com.guru.future.common.entity.dto.ContractOpenGapDTO;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.utils.FutureDateUtil;
import com.guru.future.common.utils.FutureUtil;
import com.guru.future.common.utils.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import static com.guru.future.common.utils.FutureUtil.PERCENTAGE_SYMBOL;

/**
 * @author j
 */
@Service
@Slf4j
public class FutureGapService {
    @Resource
    private Environment environment;
    @Resource
    private BasicManager basicManager;
    @Resource
    private FutureDailyManager futureDailyManager;
    @Resource
    private FutureSinaManager futureSinaManager;
    @Resource
    private FutureMailManager futureMailManager;
    @Resource
    private OpenGapManager openGapManager;
    @Resource
    private ContractManager contractManager;

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
        String[] activeProfiles = environment.getActiveProfiles();
        while (FutureDateUtil.beforeBidTime() && activeProfiles.length == 0) {
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
        log.info("open gap start! realtime data size={}, {}", contractRealtimeDTOList.size(), contractRealtimeDTOList.get(0));
        Map<String, Basic> basicMap = basicManager.getBasicMap();
        Map<String, Contract> contractMap = contractManager.getContractMap();
        String tradeDate = "";
        String preTradeDate;
        if (FutureDateUtil.isNight()) {
            tradeDate = FutureDateUtil.getNextTradeDate(new Date());
            preTradeDate = FutureDateUtil.latestTradeDate();
        } else {
            preTradeDate = FutureDateUtil.getLastTradeDate(new Date());
        }
        List<String> codes = contractManager.getContractCodes();
        Map<String, TradeDailyDO> preDailyMap = futureDailyManager.getFutureDailyMap(preTradeDate, codes);
        List<ContractOpenGapDTO> openGapDTOList = new ArrayList<>();
        int total = 0;
        Map<String, Integer> openStats = new HashMap<>();
        String openHighTag = "高开";
        String openLowTag = "低开";
        for (ContractRealtimeDTO realtimeDTO : contractRealtimeDTOList) {
            String code = realtimeDTO.getCode();
            if (Strings.isBlank(tradeDate)) {
                tradeDate = realtimeDTO.getTradeDate();
            }
            Basic basic = basicMap.get(FutureUtil.code2Symbol(code));
            int nightTrade = basic.getNight();
            if (FutureDateUtil.isNight()) {
                if (nightTrade == 0) {
                    log.warn("{} night trade not support", code);
                    continue;
                }
            } else {
                if (nightTrade != 0) {
                    continue;
                }
            }
            TradeDailyDO lastDailyDO = preDailyMap.get(realtimeDTO.getCode());
            if (lastDailyDO == null) {
                log.warn("{} last daily data missed! trade date:{}", realtimeDTO.getCode(), preTradeDate);
                continue;
            }
            BigDecimal preClose = lastDailyDO.getClose();
            BigDecimal preSettle = lastDailyDO.getSettle();
            BigDecimal preOpen = lastDailyDO.getOpen();
            BigDecimal preHigh = lastDailyDO.getHigh();
            BigDecimal preLow = lastDailyDO.getLow();
            BigDecimal currentOpen = realtimeDTO.getOpen();
            if (currentOpen.compareTo(preOpen) == 0) {
                log.warn("{} 昨开等于今开 {}!!!", realtimeDTO.getName(), currentOpen);
                continue;
            }
            BigDecimal settleDiff = currentOpen.subtract(preSettle);
            BigDecimal openSettleChange = settleDiff.multiply(BigDecimal.valueOf(100)).divide(preSettle, 2, RoundingMode.HALF_UP);
            BigDecimal priceDiff = currentOpen.subtract(preClose);
            BigDecimal openCloseChange = priceDiff.multiply(BigDecimal.valueOf(100)).divide(preClose, 2, RoundingMode.HALF_UP);
            total += 1;
            boolean isUp = settleDiff.compareTo(BigDecimal.ZERO) > 0;
            if (settleDiff.compareTo(BigDecimal.ZERO) == 0) {
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
            BigDecimal dayGap = openCloseChange;
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
                    remark.append("跳空高开 ").append("+").append(dayGap).append("%");
                } else {
                    suggestFrom = preClose.multiply(BigDecimal.valueOf(0.999));
                    remark.append("高开 ").append("+").append(openCloseChange).append("%");
                }
                if (Math.abs(openCloseChange.floatValue()) >= 0.6) {
                    suggestFrom = currentOpen.multiply(BigDecimal.valueOf(0.9964));
                }
                if (Math.abs(openCloseChange.floatValue()) >= 5) {
                    suggestTo = BigDecimal.valueOf(999999);
                } else if (Math.abs(openCloseChange.floatValue()) >= 2) {
                    BigDecimal factor = BigDecimal.ONE.add(openCloseChange.divide(BigDecimal.valueOf(100)));
                    suggestTo = currentOpen.multiply(factor);
                } else if (Math.abs(openCloseChange.floatValue()) >= 1) {
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
                    remark.append("跳空低开 ").append(dayGap).append("%");
                } else {
                    suggestFrom = currentOpen.multiply(BigDecimal.valueOf(0.999));
                    remark.append("低开 ").append(openCloseChange).append("%");
                }
                suggestTo = currentOpen.multiply(BigDecimal.valueOf(1.007));
            }
            suggestFrom = suggestFrom.setScale(1, RoundingMode.HALF_UP);
            suggestTo = suggestTo.setScale(1, RoundingMode.HALF_UP);
//            String suggestPrice = suggestFrom.intValue() + "多\n"
//                    + suggestTo.intValue() + "空";
            String suggestPrice = isUp ? suggestFrom.intValue() + "多" : suggestTo.intValue() + "空";
            Contract contract = contractMap.get(code);
            int calcPosition = FutureUtil.calcPosition(isUp, realtimeDTO.getOpen(), contract.getHigh(), contract.getLow());
            ContractOpenGapDTO openGapDTO = ContractOpenGapDTO.builder()
                    .tradeDate(tradeDate).code(code).name(basic.getName()).category(basic.getType()).dayGap(isDayGap)
                    .preClose(preClose.setScale(1, RoundingMode.HALF_UP)).preSettle(preSettle)
                    .preHigh(preHigh).preLow(preLow).open(currentOpen)
                    .openChange(openSettleChange).gapRate(dayGap).contractPosition(calcPosition)
                    .remark(remark.toString()).buyLow(suggestFrom).sellHigh(suggestTo)
                    .suggestStr(suggestPrice).suggest(isUp ? 1 : -1).suggestPrice(isUp ? suggestFrom : suggestTo).build();
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
                stringBuilder.append("<td style=\"text-align:left\">" + openGapDTO.getSuggestStr() + "</td>");
                stringBuilder.append("</tr>");
            }
        }
        stringBuilder.append("</table>");
        stringBuilder.append("</body></html>");
        futureMailManager.sendHtmlMail("缺口报告", stringBuilder.toString());
    }
}

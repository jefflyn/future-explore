package com.guru.future.biz.service;

import com.guru.future.biz.handler.FutureTaskDispatcher;
import com.guru.future.biz.manager.FutureBasicManager;
import com.guru.future.biz.manager.FutureLiveManager;
import com.guru.future.biz.manager.FutureMailManager;
import com.guru.future.common.cache.LiveDataCache;
import com.guru.future.common.entity.converter.ContractRealtimeConverter;
import com.guru.future.common.entity.dto.ContractOpenGapDTO;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.entity.vo.FutureLiveVO;
import com.guru.future.common.entity.vo.FutureOverviewVO;
import com.guru.future.common.utils.DateUtil;
import com.guru.future.common.utils.FutureUtil;
import com.guru.future.common.utils.WindowUtil;
import com.guru.future.domain.FutureBasicDO;
import com.guru.future.domain.FutureLiveDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.StatUtils;
import org.springframework.beans.BeanUtils;
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
import java.util.stream.Collectors;

import static com.guru.future.common.utils.FutureUtil.PERCENTAGE_SYMBOL;

/**
 * @author j
 */
@Service
@Slf4j
public class FutureLiveService {
    @Resource
    private FutureBasicManager futureBasicManager;

    @Resource
    private FutureLiveManager futureLiveManager;

    @Resource
    private FutureMonitorService monitorService;

    @Resource
    private FutureMailManager mailManager;


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
            BeanUtils.copyProperties(futureLiveDO, futureLiveVO);
            futureLiveVO.setWave(FutureUtil.generateWave(basicDO.getA(), basicDO.getB(), basicDO.getC(), futureLiveVO.getPrice()));
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
        WindowUtil.createMsgFrame(null);
    }

    public void refreshLiveData(List<ContractRealtimeDTO> contractRealtimeDTOList, Boolean refresh) {
        Map<String, FutureBasicDO> basicMap = futureBasicManager.getBasicMap(refresh);
        reloadLiveCache(contractRealtimeDTOList, basicMap);
        for (ContractRealtimeDTO contractRealtimeDTO : contractRealtimeDTOList) {
            FutureBasicDO futureBasicDO = basicMap.get(contractRealtimeDTO.getCode());
            if (DateUtil.isNight() && Boolean.FALSE.equals(futureBasicDO.hasNightTrade())) {
                continue;
            }
            FutureLiveDO futureLiveDO = ContractRealtimeConverter.convert2LiveDO(contractRealtimeDTO);
            futureLiveDO.setType(futureBasicDO.getType());
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

    public FutureOverviewVO getMarketOverview() {
        BigDecimal totalAvgChange = BigDecimal.ZERO;
        Map<String, List<FutureLiveDO>> categoryLiveMap = new HashMap<>();
        List<FutureLiveDO> futureLiveDOList = futureLiveManager.getAll();
        if (!CollectionUtils.isEmpty(futureLiveDOList)) {
            for (FutureLiveDO liveDO : futureLiveDOList) {
                BigDecimal change = liveDO.getChange();
                totalAvgChange = totalAvgChange.add(change);
                String type = liveDO.getType();
                List<FutureLiveDO> categoryLiveList = ObjectUtils.defaultIfNull(categoryLiveMap.get(type), new ArrayList<>());
                categoryLiveList.add(liveDO);
                categoryLiveMap.put(type, categoryLiveList);
            }
        }
        List<FutureOverviewVO.CategorySummary> categorySummaryList = new ArrayList<>();
        for (Map.Entry<String, List<FutureLiveDO>> entry : categoryLiveMap.entrySet()) {
            String category = entry.getKey();
            List<FutureLiveDO> categoryLiveList = entry.getValue();
            List<Double> changeList = categoryLiveList.stream().map(e -> e.getChange().doubleValue()).collect(Collectors.toList());
            double[] changeArr = new double[changeList.toArray().length];
            FutureLiveDO best = new FutureLiveDO();
            FutureLiveDO worst = new FutureLiveDO();
            for (int i = 0; i < categoryLiveList.size(); i++) {
                FutureLiveDO futureLiveDO = categoryLiveList.get(i);
                double change = futureLiveDO.getChange().doubleValue();
                changeArr[i] = change;
                if (best.getChange() == null || change > best.getChange().doubleValue()) {
                    best = futureLiveDO;
                }
                if (worst.getChange() == null || change < worst.getChange().doubleValue()) {
                    worst = futureLiveDO;
                }
            }
            FutureOverviewVO.CategorySummary categorySummary = new FutureOverviewVO.CategorySummary();
            categorySummary.setCategoryName(category);
            BigDecimal categoryAvgChange = BigDecimal.valueOf(StatUtils.mean(changeArr)).setScale(2, RoundingMode.HALF_UP);
            categorySummary.setAvgChange(categoryAvgChange);
            categorySummary.setAvgChangeStr((categoryAvgChange.floatValue() > 0 ? "+" : "") + categoryAvgChange + PERCENTAGE_SYMBOL);
            categorySummary.setBestName(best.getName());
            categorySummary.setBestChange((best.getChange().floatValue() > 0 ? "+" : "") + best.getChange() + PERCENTAGE_SYMBOL);
            categorySummary.setWorstName(worst.getName());
            categorySummary.setWorstChange((worst.getChange().floatValue() > 0 ? "+" : "") + worst.getChange() + PERCENTAGE_SYMBOL);
            categorySummaryList.add(categorySummary);
        }
        Collections.sort(categorySummaryList);
        if (totalAvgChange.compareTo(BigDecimal.ZERO) > 0) {
            Collections.reverse(categorySummaryList);
        }
        totalAvgChange = totalAvgChange.divide(BigDecimal.valueOf(futureLiveDOList.size()), 2, RoundingMode.HALF_UP);
        FutureOverviewVO overviewVO = new FutureOverviewVO();
        overviewVO.setTotalAvgChangeStr((totalAvgChange.floatValue() > 0 ? "+" : "") + totalAvgChange + PERCENTAGE_SYMBOL);
        overviewVO.setOverviewDesc(overviewDesc(totalAvgChange.floatValue()));
        overviewVO.setCategorySummaryList(categorySummaryList);
        return overviewVO;
    }

    private String overviewDesc(float change) {
        if (change > 0) {
            return change > 1.5 ? "多" : "偏多";
        } else {
            return Math.abs(change) > 1.5 ? "空" : "偏空";
        }
    }

    public void sendMarketOverviewMail() throws Exception {
        FutureOverviewVO overviewVO = getMarketOverview();
        String upStyle = "color:red;\"";
        StringBuilder content = new StringBuilder();
        content.append("\r\n");
        content.append("<html><head></head><body>");
        String head3 = "<h3 style=\"" + (overviewVO.getTotalAvgChangeStr().contains("+") ? upStyle : "\"") + ">";
        content.append(head3);
        content.append("市场平均涨幅: " + overviewVO.getTotalAvgChangeStr() + "【" + overviewVO.getOverviewDesc() + "】").append("</h3>");
        content.append("<table cellspacing=\"0\" cellpadding=\"1\" border=\"1\" style=\"border:solid 1px #E8F2F9;font-size=14px;;font-size:12px;\">");
        content.append("<tr style=\"background-color: #428BCA; color:#ffffff\">" +
                "<th width=\"10px\">序号</th>" +
                "<th width=\"40px\">板块</th>" +
                "<th width=\"30px\">涨跌幅</th>" +
                "<th width=\"50px\">领涨品种</th>" +
                "<th width=\"30px\">涨跌幅</th>" +
                "<th width=\"50px\">领涨品种</th>" +
                "<th width=\"30px\">涨跌幅</th>" +
                "</tr>");
        int seq = 0;
        for (FutureOverviewVO.CategorySummary categorySummary : overviewVO.getCategorySummaryList()) {
            content.append("</tr>");
            content.append("<td style=\"text-align:left\">" + (++seq) + "</td>");
            content.append("<td style=\"text-align:left;" + (categorySummary.getAvgChangeStr().contains("+") ? upStyle : "\"") + ">" + categorySummary.getCategoryName() + "</td>");
            content.append("<td style=\"text-align:center;" + (categorySummary.getAvgChangeStr().contains("+") ? upStyle : "\"") + ">" + categorySummary.getAvgChangeStr() + "</td>");
            content.append("<td style=\"text-align:left;" + (categorySummary.getBestChange().contains("+") ? upStyle : "\"") + ">" + categorySummary.getBestName() + "</td>");
            content.append("<td style=\"text-align:center;" + (categorySummary.getBestChange().contains("+") ? upStyle : "\"") + ">" + categorySummary.getBestChange() + "</td>");
            content.append("<td style=\"text-align:left;" + (categorySummary.getWorstChange().contains("+") ? upStyle : "\"") + ">" + categorySummary.getWorstName() + "</td>");
            content.append("<td style=\"text-align:center;" + (categorySummary.getWorstChange().contains("+") ? upStyle : "\"") + ">" + categorySummary.getWorstChange() + "</td>");
            content.append("</tr>");
        }
        content.append("</table>");
        content.append("</body></html>");
        mailManager.sendHtmlMail("市场概况", content.toString());
    }
}

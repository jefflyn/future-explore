package com.guru.future.biz.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.guru.future.biz.handler.FutureTaskDispatcher;
import com.guru.future.biz.manager.BasicManager;
import com.guru.future.biz.manager.ContractManager;
import com.guru.future.biz.manager.FutureLiveManager;
import com.guru.future.biz.manager.FutureMailManager;
import com.guru.future.biz.manager.WaveManager;
import com.guru.future.common.cache.LiveDataCache;
import com.guru.future.common.entity.converter.ContractRealtimeConverter;
import com.guru.future.common.entity.dao.ContractDO;
import com.guru.future.common.entity.dao.FutureLiveDO;
import com.guru.future.common.entity.domain.Basic;
import com.guru.future.common.entity.domain.Contract;
import com.guru.future.common.entity.domain.Wave;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.entity.vo.FutureLiveVO;
import com.guru.future.common.entity.vo.FutureOverviewVO;
import com.guru.future.common.utils.FutureDateUtil;
import com.guru.future.common.utils.FutureUtil;
import com.guru.future.common.utils.NullUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.logging.log4j.util.Strings;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.guru.future.common.utils.FutureDateUtil.COMMON_DATE_PATTERN;
import static com.guru.future.common.utils.FutureUtil.PERCENTAGE_SYMBOL;
import static com.guru.future.common.utils.FutureUtil.calcPosition;
import static com.guru.future.common.utils.NumberUtil.price2String;

/**
 * @author j
 */
@Service
@Slf4j
public class FutureLiveService {
    @Resource
    private RedissonClient redissonClient;

    private static final List<Pair<String, String>> NIGHT_TIMES = Arrays.asList(
            Pair.of("21:05", "21:10"),
            Pair.of("21:25", "21:30"),
            Pair.of("21:45", "21:50"),
            Pair.of("22:05", "22:10"),
            Pair.of("22:25", "22:30"),
            Pair.of("22:45", "22:50")
    );
    private static final List<Pair<String, String>> MORN_TIMES = Arrays.asList(
            Pair.of("09:05", "09:10"),
            Pair.of("09:25", "09:25"),
            Pair.of("09:45", "09:50"),
            Pair.of("10:05", "10:15"),
            Pair.of("10:35", "10:40"),
            Pair.of("10:55", "11:00")
    );
    private static final List<Pair<String, String>> NOON_TIMES = Arrays.asList(
            Pair.of("11:15", "11:20"),
            Pair.of("13:35", "13:40"),
            Pair.of("13:55", "14:00"),
            Pair.of("14:15", "14:20"),
            Pair.of("14:35", "14:40"),
            Pair.of("14:55", "15:00")
    );

    @Resource
    private BasicManager basicManager;

    @Resource
    private WaveManager waveManager;

    @Resource
    private ContractManager contractManager;

    @Resource
    private FutureLiveManager futureLiveManager;

    @Resource
    private FutureMonitorService monitorService;

    @Resource
    private FutureMailManager mailManager;

    public void refreshLiveData() {
        futureLiveManager.removeAllData();
        log.info("delete all live data");
    }

    @Async
    public void reloadLiveCache(List<ContractRealtimeDTO> contractRealtimeDTOList, Map<String, Basic> basicMap,
                                Map<String, Wave> waveMap) {
        List<FutureLiveVO> futureLiveVOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(contractRealtimeDTOList)) {
            return;
        }
        for (ContractRealtimeDTO contractRealtimeDTO : contractRealtimeDTOList) {
            String code = contractRealtimeDTO.getCode();
            Basic basicDO = basicMap.get(contractRealtimeDTO.getSymbol());
            if (Boolean.TRUE.equals(FutureDateUtil.isNight()) && basicDO.getNight() == 0) {
                continue;
            }
            FutureLiveDO futureLiveDO = ContractRealtimeConverter.convert2LiveDO(contractRealtimeDTO);
            FutureLiveVO futureLiveVO = new FutureLiveVO();
            BeanUtils.copyProperties(futureLiveDO, futureLiveVO);
            Wave wave = waveMap.get(code);
            futureLiveVO.setWave(FutureUtil.generateWave(wave.getAp(), wave.getBp(), wave.getCp(), wave.getDp(),
                    futureLiveVO.getPrice()));
            futureLiveVOList.add(futureLiveVO);
            if (code.startsWith("SC")) {
                LiveDataCache.scInfo = code + "【" + futureLiveDO.getLow() + "-" + futureLiveDO.getHigh() + "】"
                        + futureLiveVO.getPrice() + "【" + futureLiveDO.getPosition() + "】"
                        + futureLiveDO.getChange() + PERCENTAGE_SYMBOL;
            }
        }
        int size = futureLiveVOList.size();
        int topN = size > 10 ? 10 : size;
        // position
        Collections.sort(futureLiveVOList);
        List<FutureLiveVO> lowTop10List = futureLiveVOList.subList(0, topN);
        List<FutureLiveVO> highTop10List = futureLiveVOList.subList(size - topN, size);
        Collections.reverse(highTop10List);
        LiveDataCache.setPositionHighTop10(highTop10List);
        LiveDataCache.setPositionLowTop10(lowTop10List);
        // change
        Collections.sort(futureLiveVOList, (o1, o2) -> o1.getChange().compareTo(o2.getChange()));
        List<FutureLiveVO> changeLowTop10List = futureLiveVOList.subList(0, topN);
        List<FutureLiveVO> changeHighTop10List = futureLiveVOList.subList(size - topN, size);
        Collections.reverse(changeHighTop10List);
        LiveDataCache.setChangeHighTop10(changeHighTop10List);
        LiveDataCache.setChangeLowTop10(changeLowTop10List);
    }

    public void refreshLiveData(List<ContractRealtimeDTO> contractRealtimeDTOList, Boolean refresh) {
        Map<String, Basic> basicMap = basicManager.getBasicMap(refresh);
        Map<String, Wave> waveMap = waveManager.getWaveMap();
        Map<String, Contract> contractMap = contractManager.getContractMap();
        reloadLiveCache(contractRealtimeDTOList, basicMap, waveMap);
        for (ContractRealtimeDTO contractRealtimeDTO : contractRealtimeDTOList) {
            Basic basic = basicMap.get(contractRealtimeDTO.getSymbol());
            if (basic == null || (FutureDateUtil.isNight() && basic.getNight() == 0)) {
                continue;
            }
            FutureLiveDO futureLiveDO = ContractRealtimeConverter.convert2LiveDO(contractRealtimeDTO);
            futureLiveDO.setType(basic.getType());
            futureLiveDO.setTemp(basic.getRelative());
            Pair<BigDecimal, BigDecimal> highLow = updateHistHighLow(contractRealtimeDTO, contractMap.get(futureLiveDO.getCode()),
                    waveMap.get(futureLiveDO.getCode()));
            BigDecimal histHigh = highLow.getLeft();
            BigDecimal histLow = highLow.getRight();
            String histHighLowFlag = "";
            if (futureLiveDO.getHigh().compareTo(histHigh) >= 0) {
                histHighLowFlag = "合约新高";
            } else if (futureLiveDO.getLow().compareTo(histLow) <= 0) {
                histHighLowFlag = "合约新低";
            }
            monitorService.monitorPriceFlash(futureLiveDO, histHighLowFlag);
            monitorService.addPositionLog(futureLiveDO, histHighLowFlag);
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

    private Pair<BigDecimal, BigDecimal> updateHistHighLow(ContractRealtimeDTO contractRealtimeDTO, Contract contract, Wave wave) {
        BigDecimal histHigh = contract.getHigh();
        BigDecimal histLow = contract.getLow();
        boolean isLowZero = BigDecimal.ZERO.compareTo(histLow) == 0;
        boolean isHighZero = BigDecimal.ZERO.compareTo(histHigh) == 0;
        if (contractRealtimeDTO.getLow().compareTo(histLow) < 0 || isLowZero) {
            ContractDO updateContractDO = new ContractDO();
            updateContractDO.setCode(contractRealtimeDTO.getCode());
            BigDecimal newLow = contractRealtimeDTO.getPrice().compareTo(contractRealtimeDTO.getLow()) < 0 ?
                    contractRealtimeDTO.getPrice() : contractRealtimeDTO.getLow();
            if (isLowZero) {
                histLow = wave.getMinP();
                newLow = histLow;
            }
            updateContractDO.setLow(newLow);
            Date time = new Date();
            updateContractDO.setLowTime(DateUtil.format(time, COMMON_DATE_PATTERN));
            updateContractDO.setUpdateTime(time);
            contractManager.updateContract(updateContractDO);
            FutureTaskDispatcher.setRefresh();
            log.info("{} update hist low, refresh contract data", contractRealtimeDTO.getCode());
        }
        if (contractRealtimeDTO.getHigh().compareTo(histHigh) > 0 || isHighZero) {
            ContractDO updateContractDO = new ContractDO();
            updateContractDO.setCode(contractRealtimeDTO.getCode());
            BigDecimal newHigh = contractRealtimeDTO.getPrice().compareTo(contractRealtimeDTO.getHigh()) > 0 ?
                    contractRealtimeDTO.getPrice() : contractRealtimeDTO.getHigh();
            if (isHighZero) {
                histHigh = wave.getMaxP();
                newHigh = histHigh;
            }
            updateContractDO.setHigh(newHigh);
            Date time = new Date();
            updateContractDO.setHighTime(DateUtil.format(time, COMMON_DATE_PATTERN));
            updateContractDO.setUpdateTime(time);
            contractManager.updateContract(updateContractDO);
            FutureTaskDispatcher.setRefresh();
            log.info("{} update hist high, refresh contract data", contractRealtimeDTO.getCode());
        }
        return Pair.of(histHigh, histLow);
    }

    public FutureOverviewVO getMarketOverview() {
        FutureOverviewVO overviewVO = new FutureOverviewVO();
        BigDecimal totalAvgChange = BigDecimal.ZERO;
        Map<String, List<FutureLiveDO>> categoryLiveMap = new HashMap<>();
        List<FutureLiveDO> futureLiveDOList = futureLiveManager.getAll();
        if (CollectionUtils.isEmpty(futureLiveDOList)) {
            return overviewVO;
        }
        Map<String, Contract> contractMap = contractManager.getContractMap();
        for (FutureLiveDO liveDO : futureLiveDOList) {
            Contract contract = contractMap.get(liveDO.getCode());
            if (contract.getMain() == 0) {
                continue;
            }
            BigDecimal change = liveDO.getChange();
            totalAvgChange = totalAvgChange.add(change);
            String type = liveDO.getType();
            List<FutureLiveDO> categoryLiveList = ObjectUtils.defaultIfNull(categoryLiveMap.get(type), new ArrayList<>());
            categoryLiveList.add(liveDO);
            categoryLiveMap.put(type, categoryLiveList);
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
            categorySummary.setAvgChangeStr((categoryAvgChange.floatValue() >= 0 ? "+" : "") + categoryAvgChange + PERCENTAGE_SYMBOL);
            categorySummary.setBestName(best.getName());
            categorySummary.setBestChange((best.getChange().floatValue() >= 0 ? "+" : "") + best.getChange() + PERCENTAGE_SYMBOL);
            categorySummary.setBestPrice(best.getPrice());
            categorySummary.setWorstName(worst.getName());
            categorySummary.setWorstChange((worst.getChange().floatValue() >= 0 ? "+" : "") + worst.getChange() + PERCENTAGE_SYMBOL);
            categorySummary.setWorstPrice(worst.getPrice());
            categorySummaryList.add(categorySummary);
        }
        Collections.sort(categorySummaryList);
        if (totalAvgChange.compareTo(BigDecimal.ZERO) > 0) {
            Collections.reverse(categorySummaryList);
        }
        int sortNo = 0;
        for (FutureOverviewVO.CategorySummary categorySummary : categorySummaryList) {
            categorySummary.setSortNo(++sortNo);
        }
        totalAvgChange = totalAvgChange.divide(BigDecimal.valueOf(futureLiveDOList.size()), 2, RoundingMode.HALF_UP);

        overviewVO.setTotalAvgChangeStr((totalAvgChange.floatValue() >= 0 ? "+" : "") + totalAvgChange + PERCENTAGE_SYMBOL);
        overviewVO.setOverviewDesc(overviewDesc(totalAvgChange.floatValue()));
        overviewVO.setHistOverviewDesc(getHistOverview(totalAvgChange));
        overviewVO.setCategorySummaryList(categorySummaryList);
        return overviewVO;
    }

    private String getHistOverview(BigDecimal totalAvgChange) {
        StringBuilder histOverviewStr = new StringBuilder();
        String key = FutureDateUtil.currentTradeDate() + "_overview";
        RList<Map<String, String>> cacheList = redissonClient.getList(key);
        if (CollUtil.isNotEmpty(cacheList)) {
            appendOverviewStr(histOverviewStr, totalAvgChange, cacheList);
        }
        return histOverviewStr.toString();
    }

    private void appendOverviewStr(StringBuilder histOverviewStr, BigDecimal totalAvgChange, RList<Map<String, String>> cacheList) {
        Map<String, String> timeChangeMap = new HashMap<>();
        BigDecimal highChange = BigDecimal.ZERO;
        BigDecimal lowChange = BigDecimal.ZERO;
        String closeChange = "";
        for (int i = 0; i < cacheList.size(); i++) {
            Map<String, String> map = cacheList.get(i);
            timeChangeMap.putAll(map);
            closeChange = map.values().iterator().next();
            if (Strings.isNotBlank(closeChange)) {
                BigDecimal change = BigDecimal.valueOf(Double.valueOf(closeChange.replace("%", "")))
                        .setScale(2, RoundingMode.HALF_UP);
                if (i == 0) {
                    highChange = change;
                    lowChange = change;
                }
                if (change.compareTo(highChange) > 0) {
                    highChange = change;
                }
                if (change.compareTo(lowChange) < 0) {
                    lowChange = change;
                }
            }
        }
        int changePos = 0;
        if (totalAvgChange != null) {
            changePos = calcPosition(totalAvgChange.floatValue() > 0, totalAvgChange, highChange, lowChange);
        }
        BigDecimal categoryAvgChange = BigDecimal.valueOf(Double.valueOf(closeChange.replace("%", ""))).setScale(2, RoundingMode.HALF_UP);
        histOverviewStr.append(changePos).append("【").append(categoryAvgChange.floatValue() >= 0 ? "+" : "").append(categoryAvgChange).append("】");
        setHistOverview(histOverviewStr, NIGHT_TIMES, timeChangeMap);
        histOverviewStr.append("| ");
        histOverviewStr.append("\n");
        setHistOverview(histOverviewStr, MORN_TIMES, timeChangeMap);
        histOverviewStr.append("| ");
        setHistOverview(histOverviewStr, NOON_TIMES, timeChangeMap);
    }

    /**
     * hist overview
     *
     * @param histOverview
     * @param timeList
     * @param overviewMap
     * @return low high change
     */
    private void setHistOverview(StringBuilder histOverview, List<Pair<String, String>> timeList, Map<String, String> overviewMap) {
        for (Pair<String, String> time : timeList) {
            String timeChange = NullUtil.defaultValue(overviewMap.get(time.getLeft()), overviewMap.get(time.getRight()));
            histOverview.append(timeChange).append(" ");
        }
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
        content.append("指数: " + overviewVO.getTotalAvgChangeStr() + "【" + overviewVO.getOverviewDesc() + "】").append("</h3>");
        content.append("<table cellspacing=\"0\" cellpadding=\"1\" border=\"1\" style=\"border:solid 1px #E8F2F9;font-size=14px;;font-size:12px;\">");
        content.append("<tr style=\"background-color: #428BCA; color:#ffffff\">" +
                "<th width=\"10px\">序号</th>" +
                "<th width=\"40px\">板块</th>" +
                "<th width=\"30px\">涨跌幅</th>" +
                "<th width=\"50px\">强势品种</th>" +
                "<th width=\"30px\">涨跌幅</th>" +
                "<th width=\"50px\">弱势品种</th>" +
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
        content.append("<br/>");
        content.append(highTopContent());
        content.append("<br/>");
        content.append(lowTopContent());
        content.append("</body></html>");
        mailManager.sendHtmlMail("市场概况", content.toString());
    }

    private String highTopContent() {
        StringBuilder content = new StringBuilder();
        content.append("<table cellspacing=\"0\" cellpadding=\"1\" border=\"1\" style=\"border:solid 1px #E8F2F9;font-size=14px;;font-size:12px;\">");
        content.append("<tr style=\"background-color: #428BCA; color:#ffffff\">" +
                "<th width=\"10px\">序号</th>" +
                "<th width=\"50px\">名称</th>" +
                "<th width=\"40px\">价格</th>" +
                "<th width=\"30px\">涨跌幅</th>" +
                "<th width=\"30px\">相对位置</th>" +
                "<th width=\"80px\">波段</th>" +
                "</tr>");
        for (int i = 0; i < LiveDataCache.getPositionHighTop10().size(); i++) {
            FutureLiveVO highTop = LiveDataCache.getPositionHighTop10().get(i);
            content.append("</tr>");
            content.append("<td style=\"text-align:left\">" + highTop.getSortNo() + "</td>");
            content.append("<td style=\"text-align:left\">" + highTop.getName() + "</td>");
            content.append("<td style=\"text-align:center\">" + price2String(highTop.getPrice()) + "</td>");
            content.append("<td style=\"text-align:left\">" + highTop.getChange() + FutureUtil.PERCENTAGE_SYMBOL + "</td>");
            content.append("<td style=\"text-align:center\">" + highTop.getPosition() + "</td>");
            content.append("<td style=\"text-align:left\">" + highTop.getWave() + "</td>");
            content.append("</tr>");
        }
        content.append("</table>");
        return content.toString();
    }

    private String lowTopContent() {
        StringBuilder content = new StringBuilder();
        content.append("<table cellspacing=\"0\" cellpadding=\"1\" border=\"1\" style=\"border:solid 1px #E8F2F9;font-size=14px;;font-size:12px;\">");
        content.append("<tr style=\"background-color: #428BCA; color:#ffffff\">" +
                "<th width=\"10px\">序号</th>" +
                "<th width=\"50px\">名称</th>" +
                "<th width=\"40px\">价格</th>" +
                "<th width=\"30px\">涨跌幅</th>" +
                "<th width=\"30px\">相对位置</th>" +
                "<th width=\"80px\">波段</th>" +
                "</tr>");
        for (int i = 0; i < LiveDataCache.getPositionLowTop10().size(); i++) {
            FutureLiveVO lowTop = LiveDataCache.getPositionLowTop10().get(i);
            content.append("</tr>");
            content.append("<td style=\"text-align:left\">" + lowTop.getSortNo() + "</td>");
            content.append("<td style=\"text-align:left\">" + lowTop.getName() + "</td>");
            content.append("<td style=\"text-align:center\">" + price2String(lowTop.getPrice()) + "</td>");
            content.append("<td style=\"text-align:left\">" + lowTop.getChange() + FutureUtil.PERCENTAGE_SYMBOL + "</td>");
            content.append("<td style=\"text-align:center\">" + lowTop.getPosition() + "</td>");
            content.append("<td style=\"text-align:left\">" + lowTop.getWave() + "</td>");
            content.append("</tr>");
        }
        content.append("</table>");
        return content.toString();
    }

    public String showHistOverview() {
        StringBuilder result = new StringBuilder();
        String currentDate = FutureDateUtil.currentTradeDate();
        String key = currentDate + "_overview";
        RList<Map<String, String>> cacheList = redissonClient.getList(key);
        while (!cacheList.isEmpty()) {
            result.append(currentDate).append(":");
            appendOverviewStr(result, null, cacheList);
            result.append("\n");
            currentDate = FutureDateUtil.getLastTradeDate(currentDate);
            key = currentDate + "_overview";
            cacheList = redissonClient.getList(key);
        }
        return result.toString();
    }
}

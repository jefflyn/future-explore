package com.guru.future.biz.service;

import com.guru.future.biz.manager.FutureLogManager;
import com.guru.future.biz.manager.StatManager;
import com.guru.future.biz.manager.base.FutureCacheManager;
import com.guru.future.common.cache.PriceFlashCache;
import com.guru.future.common.entity.dao.FutureLiveDO;
import com.guru.future.common.entity.dao.FutureLogDO;
import com.guru.future.common.entity.domain.NDayStat;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.enums.LogType;
import com.guru.future.common.enums.OptionType;
import com.guru.future.common.ui.FutureFrame;
import com.guru.future.common.utils.FutureDateUtil;
import com.guru.future.common.utils.FutureUtil;
import com.guru.future.common.utils.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.javatuples.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import static com.guru.future.common.utils.NumberUtil.price2String;

@Service
@Slf4j
public class FutureMonitorService {
    private static List<Pair<Integer, Float>> monitorParams = new ArrayList<>();
    private static LinkedList<String> contents = new LinkedList<>();
    private static Map<String, Map<BigDecimal, LongAdder>> positionCount = new HashMap<>();

    static {
        monitorParams.add(Pair.with(30, 0.33F));
        monitorParams.add(Pair.with(60, 0.66F));
        monitorParams.add(Pair.with(120, 1F));
    }

    @Resource
    private FutureCacheManager futureCacheManager;

    @Resource
    private FutureLogManager futureLogManager;

    @Resource
    private StatManager statManager;

    public void monitorPriceFlash(FutureLiveDO futureLiveDO, String histHighLowFlag) {
        if (Boolean.TRUE.equals(FutureDateUtil.isPriceMonitorTime())) {
            try {
                for (Pair<Integer, Float> param : monitorParams) {
                    triggerPriceFlash(param, futureLiveDO, histHighLowFlag);
                }
            } catch (Exception e) {
                log.error("monitorPriceFlash fail, error={}", e);
            }
        }
    }

    @Async
    @Transactional
    public void addPositionLog(FutureLiveDO futureLiveDO, String histHighLowFlag) {
        int position = -1;
        if (futureLiveDO.getPosition() <= 0) {
            position = 0;
        } else if (futureLiveDO.getPosition() >= 100) {
            position = 100;
        }
        if (position > -1) {
            String contentStr = futureLiveDO.getCode() + futureLiveDO.getPrice() + futureLiveDO.getPosition();
            if (Boolean.TRUE.equals(contents.contains(contentStr))) {
                return;
            }
            contents.addFirst(contentStr);
            if (contents.size() > 10) {
                contents.removeLast();
            }
            boolean addLog = false;
            String positionKey = "_" + futureLiveDO.getCode() + "_" + position;
            // 保存上一次的价格和次数
            Map<BigDecimal, LongAdder> priceAdderMap = positionCount.get(positionKey);
            if (priceAdderMap == null) {
                Integer cacheCount = NumberUtil.toInteger(futureCacheManager.get(FutureDateUtil.currentTradeDate() + positionKey));
                priceAdderMap = new HashMap<>();
                LongAdder adder = new LongAdder();
                adder.add(cacheCount.longValue());
                adder.increment();
                priceAdderMap.put(futureLiveDO.getPrice(), adder);
                positionCount.put(positionKey, priceAdderMap);
                futureCacheManager.put(FutureDateUtil.currentTradeDate() + positionKey, adder.intValue(), 6L, TimeUnit.HOURS);
                addLog = true;
            } else {
                LongAdder lastAdder = priceAdderMap.values().stream().findFirst().get();
                LongAdder adder = priceAdderMap.get(futureLiveDO.getPrice());
                BigDecimal lastPrice = priceAdderMap.keySet().iterator().next();
                boolean newRecord = position == 0 ? futureLiveDO.getPrice().compareTo(lastPrice) < 0
                        : futureLiveDO.getPrice().compareTo(lastPrice) > 0;
                if (adder == null && newRecord) {
                    adder = new LongAdder();
                    adder.add(lastAdder.intValue());
                    adder.increment();
                    priceAdderMap.clear();
                    priceAdderMap.put(futureLiveDO.getPrice(), adder);
                    positionCount.put(positionKey, priceAdderMap);
                    futureCacheManager.put(FutureDateUtil.currentTradeDate() + positionKey, adder.intValue(), 6L, TimeUnit.HOURS);
                    addLog = true;
                }
            }
            if (addLog) {
                FutureLogDO futureLogDO = new FutureLogDO();
                futureLogDO.setTradeDate(FutureDateUtil.currentTradeDate());
                futureLogDO.setCode(futureLiveDO.getCode());
                futureLogDO.setFactor(priceAdderMap.values().stream().findFirst().orElse(new LongAdder()).intValue());
                futureLogDO.setName(futureLiveDO.getName());
                String type = "日内低点";
                if (position == 0) {
                    futureLogDO.setOption(OptionType.SELL_SHORT.getDesc());
                } else {
                    type = "日内高点";
                    futureLogDO.setOption(OptionType.BUY_LONG.getDesc());
                }
                String content = Strings.isNotBlank(histHighLowFlag) ? histHighLowFlag : type;
                futureLogDO.setType(type);
                futureLogDO.setContent(content);

                futureLogDO.setSuggest(futureLiveDO.getPrice());
                futureLogDO.setPctChange(futureLiveDO.getChange());
                futureLogDO.setPosition(futureLiveDO.getPosition());
                this.msgNotice(futureLogDO);
//            futureLogManager.deleteLogByType(futureLogDO.getCode(), futureLogDO.getTradeDate(), futureLogDO.getType());
                futureLogManager.addFutureLog(futureLogDO);
                log.info("add position log >>> {}, {}, {}", futureLogDO.getName(), futureLogDO.getPosition(), futureLogDO.getSuggest());
            }
        }
    }

    @Async
    public void triggerPriceFlash(Pair<Integer, Float> param, FutureLiveDO futureLiveDO, String histHighLowFlag) {
        int factor = param.getValue0();
        float triggerDiff = param.getValue1();
        String code = futureLiveDO.getCode();
        String cachedKey = code + param;
        BigDecimal price = futureLiveDO.getPrice();
        PriceFlashCache.rPush(cachedKey, price);
        int priceLen = PriceFlashCache.length(cachedKey);
        if (priceLen == 1) {
            return;
        }
        int steps = factor / 2;
        BigDecimal lastPrice;
        if (priceLen >= steps) {
            lastPrice = PriceFlashCache.lPop(cachedKey);
        } else {
            lastPrice = PriceFlashCache.peekFirst(cachedKey);
        }
        Pair<BigDecimal, BigDecimal> minMaxPrices = PriceFlashCache.getMinAndMaxFromList(cachedKey);
        BigDecimal minPrice = minMaxPrices.getValue0();
        BigDecimal maxPrice = minMaxPrices.getValue1();
        boolean isTrigger = false;
        String blastTip = "";
        BigDecimal change = FutureUtil.calcChange(price, maxPrice);
        float diff = change.floatValue();
        if (Math.abs(diff) >= triggerDiff) {
            isTrigger = true;
            lastPrice = maxPrice;
            if (PriceFlashCache.index(cachedKey, maxPrice) > (priceLen / 2)) {
                blastTip = "+";
            }
        } else {
            change = FutureUtil.calcChange(price, minPrice);
            diff = change.floatValue();
            if (Math.abs(diff) >= triggerDiff) {
                isTrigger = true;
                lastPrice = minPrice;
                if (PriceFlashCache.index(cachedKey, minPrice) > (priceLen / 2)) {
                    blastTip = "+";
                }
            }
        }

        if (isTrigger) {
            boolean isUp = price.compareTo(lastPrice) > 0;
            BigDecimal suggestPrice = lastPrice;
            String logType = (isUp ? "上涨" : "下跌") + blastTip;
            String suggestParam = (isUp ? OptionType.BUY_LONG.getDesc() : OptionType.SELL_SHORT.getDesc());
            String positionStr = futureLiveDO.getPosition() > 85 ? "高位" : futureLiveDO.getPosition() < 15 ? "低位" : "";
            StringBuilder content = new StringBuilder();
            content.append(positionStr + logType + (Strings.isNotBlank(positionStr) ? "!!!" : ""));
            FutureLogDO futureLogDO = new FutureLogDO();
            futureLogDO.setTradeDate(FutureDateUtil.currentTradeDate());
            futureLogDO.setCode(futureLiveDO.getCode());
            futureLogDO.setFactor(factor);
            futureLogDO.setDiff(change);
            futureLogDO.setOption(suggestParam);
            futureLogDO.setName(futureLiveDO.getName());
            futureLogDO.setType(logType);
            futureLogDO.setContent(content.toString());
            futureLogDO.setSuggest(suggestPrice);
            futureLogDO.setPctChange(futureLiveDO.getChange());
            futureLogDO.setPosition(futureLiveDO.getPosition());
            futureLogDO.setRemark(logType + " " + histHighLowFlag);
            this.msgNotice(futureLogDO);
            futureLogManager.addFutureLog(futureLogDO);
            log.info("add price flash log >>> {}, {}, {}, {}", logType, futureLogDO.getName(), futureLogDO.getDiff(), futureLogDO.getSuggest());
//            collectService.scheduleTradeDailyCollect(Lists.newArrayList(code), CollectType.COLLECT_SCHEDULE);
            // 删除价格列表，重新获取
            PriceFlashCache.delete(cachedKey);
        }
    }

    @Async
    public void monitorMa(FutureLiveDO futureLiveDO) {
        String code = futureLiveDO.getCode();
        NDayStat nDayStat = statManager.getDayStat(code);
        BigDecimal price = futureLiveDO.getPrice();
        if (nDayStat != null) {
            // avg-5
            if (nDayStat.isMa5Up()) {
                String key = code + LogType.MA_DOWN_5.getDesc();
                if (price.floatValue() < nDayStat.getAvg5d() && futureCacheManager.get(code) != null) {
                    FutureLogDO futureLogDO = buildMaFutureLogDO(futureLiveDO, LogType.MA_DOWN_5.getDesc(), OptionType.SELL_SHORT.getDesc(), new BigDecimal(nDayStat.getAvg5d()));
                    this.msgNotice(futureLogDO);
                    futureLogManager.addFutureLog(futureLogDO);
                    futureCacheManager.put(key, nDayStat.getAvg5d(), 100, TimeUnit.DAYS);
                }
            } else {
                if (price.floatValue() > nDayStat.getAvg5d() && futureCacheManager.get(code) != null) {
                    futureCacheManager.put(code + LogType.MA_DOWN_5.getDesc(), nDayStat.getAvg5d(), 1, TimeUnit.SECONDS);
                }
            }
            if (nDayStat.isMa5Down()) {
                String key = code + LogType.MA_UP_5.getDesc();
                if (futureCacheManager.get(code) != null && price.floatValue() > nDayStat.getAvg5d()) {
                    FutureLogDO futureLogDO = buildMaFutureLogDO(futureLiveDO, LogType.MA_UP_5.getDesc(), OptionType.BUY_LONG.getDesc(), new BigDecimal(nDayStat.getAvg5d()));
                    this.msgNotice(futureLogDO);
                    futureLogManager.addFutureLog(futureLogDO);
                    futureCacheManager.put(key, price, 100, TimeUnit.DAYS);
                }
            } else {
                if (futureCacheManager.get(code) != null && price.floatValue() < nDayStat.getAvg5d()) {
                    futureCacheManager.put(code + LogType.MA_UP_5.getDesc(), nDayStat.getAvg5d(), 1, TimeUnit.SECONDS);
                }
            }
            // avg-10
            if (nDayStat.isMa10Up() && price.floatValue() < nDayStat.getAvg10d()) {
                String key = code + LogType.MA_DOWN_10.getDesc();
                if (futureCacheManager.get(code) != null) {
                    FutureLogDO futureLogDO = buildMaFutureLogDO(futureLiveDO, LogType.MA_DOWN_10.getDesc(), OptionType.SELL_SHORT.getDesc(), new BigDecimal(nDayStat.getAvg5d()));
                    this.msgNotice(futureLogDO);
                    futureLogManager.addFutureLog(futureLogDO);
                    futureCacheManager.put(key, price, 30, TimeUnit.MINUTES);
                }
            }
            if (nDayStat.isMa10Down() && price.floatValue() > nDayStat.getAvg10d()) {
                String key = code + LogType.MA_UP_10.getDesc();
                if (futureCacheManager.get(code) != null) {
                    FutureLogDO futureLogDO = buildMaFutureLogDO(futureLiveDO, LogType.MA_UP_10.getDesc(), OptionType.BUY_LONG.getDesc(), new BigDecimal(nDayStat.getAvg5d()));
                    this.msgNotice(futureLogDO);
                    futureLogManager.addFutureLog(futureLogDO);
                    futureCacheManager.put(key, price, 30, TimeUnit.MINUTES);
                }
            }
            // avg-60
            if (price.floatValue() < nDayStat.getAvg60d()
                    && NumberUtil.changeDiff(price.floatValue(), nDayStat.getAvg60d().floatValue()) <= 0.15) {
                String key = code + LogType.MA_DOWN_60.getDesc();
                if (futureCacheManager.get(code) != null) {
                    FutureLogDO futureLogDO = buildMaFutureLogDO(futureLiveDO, LogType.MA_DOWN_60.getDesc(), OptionType.SELL_SHORT.getDesc(), new BigDecimal(nDayStat.getAvg5d()));
                    this.msgNotice(futureLogDO);
                    futureLogManager.addFutureLog(futureLogDO);
                    futureCacheManager.put(key, price, 30, TimeUnit.MINUTES);
                }
            }
            if (price.floatValue() > nDayStat.getAvg60d()
                    && NumberUtil.changeDiff(price.floatValue(), nDayStat.getAvg60d().floatValue()) <= 0.15) {
                String key = code + LogType.MA_UP_60.getDesc();
                if (futureCacheManager.get(code) != null) {
                    FutureLogDO futureLogDO = buildMaFutureLogDO(futureLiveDO, LogType.MA_UP_60.getDesc(), OptionType.BUY_LONG.getDesc(), new BigDecimal(nDayStat.getAvg5d()));
                    this.msgNotice(futureLogDO);
                    futureLogManager.addFutureLog(futureLogDO);
                    futureCacheManager.put(key, price, 30, TimeUnit.MINUTES);
                }
            }
        }
    }

    private FutureLogDO buildMaFutureLogDO(FutureLiveDO futureLiveDO, String logType, String option, BigDecimal suggestPrice) {
        FutureLogDO futureLogDO = new FutureLogDO();
        futureLogDO.setTradeDate(FutureDateUtil.currentTradeDate());
        futureLogDO.setCode(futureLiveDO.getCode());
        futureLogDO.setOption(option);
        futureLogDO.setName(futureLiveDO.getName());
        futureLogDO.setType(logType);
        futureLogDO.setContent(logType);
        futureLogDO.setSuggest(suggestPrice);
        futureLogDO.setPctChange(futureLiveDO.getChange());
        futureLogDO.setPosition(futureLiveDO.getPosition());
        return futureLogDO;
    }

    @Async
    public void addNewHighLowLog(ContractRealtimeDTO contractRealtimeDTO, Boolean newHigh) {
        FutureLogDO futureLogDO = new FutureLogDO();
        futureLogDO.setTradeDate(FutureDateUtil.currentTradeDate());
        futureLogDO.setCode(contractRealtimeDTO.getCode());
        futureLogDO.setFactor(-1);
        futureLogDO.setName(contractRealtimeDTO.getName());
        if (Boolean.TRUE.equals(newHigh)) {
            futureLogDO.setType("波段新高");
            futureLogDO.setContent("波段新高");
            futureLogDO.setOption(OptionType.BUY_LONG.getDesc());
        } else {
            futureLogDO.setType("波段新低");
            futureLogDO.setContent("波段新低");
            futureLogDO.setOption(OptionType.SELL_SHORT.getDesc());
        }
        futureLogDO.setSuggest(contractRealtimeDTO.getPrice());
        futureLogDO.setPctChange(FutureUtil.calcChange(contractRealtimeDTO.getPrice(), contractRealtimeDTO.getPreSettle()));
        futureLogDO.setPosition(FutureUtil.calcPosition(futureLogDO.getPctChange().floatValue() > 0, contractRealtimeDTO.getPrice(),
                contractRealtimeDTO.getHigh(), contractRealtimeDTO.getLow()));
        msgNotice(futureLogDO);
        futureLogManager.addFutureLog(futureLogDO);
        log.info("add wave log >>> {}, {}", futureLogDO.getName(), futureLogDO.getContent());
    }

    private void msgNotice(FutureLogDO futureLogDO) {
        String diffStr = Objects.isNull(futureLogDO.getDiff()) ? ""
                : " " + String.format("%.2f", futureLogDO.getDiff()) + "%";
        String factorStr = futureLogDO.getType().contains("日内") ? futureLogDO.getFactor() + "+"
                : String.valueOf(futureLogDO.getFactor());
        StringBuilder content = new StringBuilder();
        content.append(futureLogDO.getType()).append(" ").append(factorStr).append(diffStr)
                .append("【").append(futureLogDO.getContent()).append("】")
                .append(futureLogDO.getOption()).append(" ").append(price2String(futureLogDO.getSuggest()))
                .append("【").append(futureLogDO.getPosition()).append("】")
                .append(futureLogDO.getPctChange()).append("%");
        // show msg frame
        FutureFrame futureFrame = FutureFrame.buildFutureFrame(null);
        futureFrame.createMsgFrame(FutureDateUtil.currentTime() + " "
                + futureLogDO.getName() + " " + content);
    }
}

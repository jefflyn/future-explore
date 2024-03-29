package com.guru.future.biz.manager.remote;

import com.alibaba.fastjson.JSON;
import com.guru.future.common.enums.Exchange;
import com.guru.future.common.utils.FutureDateUtil;
import com.guru.future.common.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.guru.future.common.utils.FutureDateUtil.TRADE_DATE_PATTERN_FLAT;

/**
 * @author j
 */
@Component
@Slf4j
public class TsFutureManager {
    private final static String TS_URL = "http://api.tushare.pro";
    private final static String TOKEN = "f65316bb26b0a27ef7f876249615fcba99b5aab10e5be46cb278e53e";
    /**
     * 合约信息
     *
     * @param exchange     Exchange
     * @param contractType 1=普通合约 2=主力与连续合约
     * @return
     */
    public String getBasic(String exchange, Integer contractType) {
        Map<String, Object> params = new HashMap<>();
        params.put("exchange", exchange);
        params.put("fut_type", contractType);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("api_name", "fut_basic");
        paramMap.put("token", TOKEN);
        paramMap.put("params", params);

        String jsonResult = HttpUtil.doPostJson(TS_URL, JSON.toJSONString(paramMap));
        log.info("url={}, result={}", TS_URL, jsonResult);
        return jsonResult;
    }

    /**
     * 历史daily
     *
     * @param tsCode
     * @param startDate
     * @param endDate
     * @return
     */
    public String getDaily(String tsCode, String startDate, String endDate) {
        startDate = ObjectUtils.defaultIfNull(startDate, FutureDateUtil.getLastTradeDate(null, TRADE_DATE_PATTERN_FLAT));
        endDate = ObjectUtils.defaultIfNull(endDate, FutureDateUtil.getNextTradeDate(null, TRADE_DATE_PATTERN_FLAT));
        Map<String, Object> params = new HashMap<>();
        params.put("ts_code", tsCode);
        params.put("start_date", startDate);
        params.put("end_date", endDate);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("api_name", "fut_daily");
        paramMap.put("token", TOKEN);
        paramMap.put("params", params);

        String jsonResult = HttpUtil.doPostJson(TS_URL, JSON.toJSONString(paramMap));
//        log.info("url={}, result={}", TS_URL, jsonResult);
        return jsonResult;
    }

    public String getHolding(String code, String startDate, String endDate) {
        startDate = ObjectUtils.defaultIfNull(startDate, FutureDateUtil.getLastTradeDate(null, TRADE_DATE_PATTERN_FLAT));
        endDate = ObjectUtils.defaultIfNull(endDate, FutureDateUtil.getNextTradeDate(null, TRADE_DATE_PATTERN_FLAT));
        Map<String, Object> params = new HashMap<>();
        params.put("symbol", code);
        params.put("start_date", startDate);
        params.put("end_date", endDate);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("api_name", "fut_holding");
        paramMap.put("token", TOKEN);
        paramMap.put("params", params);

        String jsonResult = HttpUtil.doPostJson(TS_URL, JSON.toJSONString(paramMap));
//        log.info("url={}, result={}", TS_URL, jsonResult);
        return jsonResult;
    }

    public static void main(String[] args) {
        TsFutureManager tsFutureManager = new TsFutureManager();
        tsFutureManager.getBasic(Exchange.CZCE.getCode(), 2);
    }

}

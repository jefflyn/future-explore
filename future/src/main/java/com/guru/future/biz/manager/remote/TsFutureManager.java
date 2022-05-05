package com.guru.future.biz.manager.remote;

import com.alibaba.fastjson.JSON;
import com.guru.future.common.enums.Exchange;
import com.guru.future.common.utils.DateUtil;
import com.guru.future.common.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.guru.future.common.utils.DateUtil.TRADE_DATE_PATTERN_FLAT;
import static com.guru.future.common.utils.DateUtil.TRADE_DATE_PATTERN_HYPHEN;

/**
 * @author j
 */
@Component
@Slf4j
public class TsFutureManager {
    private final static String TS_URL = "http://api.tushare.pro";

//    private final static String TOKEN = "852fb7e0feac4bf63d6e93385344af3a91fa2b3466f3bb6955575a3b";
//    private final static String TOKEN = "3af111f9e9899366fdcedbc34cbd2491eeff7ffec6064ed0c234e2ef";
//    private final static String TOKEN = "00d803b166f55fc30c178d74c158985136010d6bd19271b182059eef";
//    private final static String TOKEN = "9fb1e3080687551cb6f8c14442233381f84afd65df3a6472b3c91e39";
//    private final static String TOKEN = "578a17c9a956cfdf85453788ffd301e9fc5eb1824a6ec507f544be56";
    private final static String TOKEN = "08cdba72469ce5ef4afd2cb5946749312d874c2aaef8ed9e176fa1a6";

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
        startDate = ObjectUtils.defaultIfNull(startDate, DateUtil.getLastTradeDate(null, TRADE_DATE_PATTERN_FLAT));
        endDate = ObjectUtils.defaultIfNull(endDate, DateUtil.getNextTradeDate(null, TRADE_DATE_PATTERN_FLAT));
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
        startDate = ObjectUtils.defaultIfNull(startDate, DateUtil.getLastTradeDate(null, TRADE_DATE_PATTERN_FLAT));
        endDate = ObjectUtils.defaultIfNull(endDate, DateUtil.getNextTradeDate(null, TRADE_DATE_PATTERN_FLAT));
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

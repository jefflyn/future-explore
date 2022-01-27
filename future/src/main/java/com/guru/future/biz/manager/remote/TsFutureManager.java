package com.guru.future.biz.manager.remote;

import com.alibaba.fastjson.JSON;
import com.guru.future.common.enums.Exchange;
import com.guru.future.common.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author j
 */
@Component
@Slf4j
public class TsFutureManager {
    private final static String TS_URL = "http://api.tushare.pro";

    private final static String TOKEN = "852fb7e0feac4bf63d6e93385344af3a91fa2b3466f3bb6955575a3b";

    /**
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

    public static void main(String[] args) {
        TsFutureManager tsFutureManager = new TsFutureManager();
        tsFutureManager.getBasic(Exchange.CZCE.getCode(), 2);
    }

}

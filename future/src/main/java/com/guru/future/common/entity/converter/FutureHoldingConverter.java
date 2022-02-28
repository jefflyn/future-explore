package com.guru.future.common.entity.converter;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.guru.future.domain.TsFutureHoldingDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class FutureHoldingConverter {

    private final static String[] HOLDING_COLS = new String[]{"trade_date", "symbol", "broker", "vol", "vol_chg", "long_hld", "long_chg", "short_hld", "short_chg"};

    public static List<TsFutureHoldingDO> toTsFutureHoldingDO(String jsonResult) {
        List<TsFutureHoldingDO> tsFutureHoldingDOList = new ArrayList<>();
        if (Strings.isNullOrEmpty(jsonResult)) {
            log.warn("没有查到数据，处理完成！");
            return tsFutureHoldingDOList;
        }
        JSONObject jsonObject = JSON.parseObject(jsonResult);
        JSONObject dataJson = (JSONObject) jsonObject.get("data");
        JSONArray fields = (JSONArray) dataJson.get("fields");
        for (int i = 0; i < HOLDING_COLS.length; i++) {
            Assert.isTrue(HOLDING_COLS[i].equals(fields.get(i)), "ts holding columns change， please check!");
        }
        JSONArray items = (JSONArray) dataJson.get("items");
        Iterator iterator = items.iterator();
        while (iterator.hasNext()) {
            JSONArray itemArr = (JSONArray) iterator.next();
            TsFutureHoldingDO futureHoldingDO = new TsFutureHoldingDO();
            futureHoldingDO.setTradeDate((String) itemArr.get(0));
            futureHoldingDO.setSymbol((String) itemArr.get(1));
            futureHoldingDO.setBroker((String) itemArr.get(2));
            futureHoldingDO.setVol((Integer) ObjectUtil.defaultIfNull(itemArr.get(3), 0));
            futureHoldingDO.setVolChg((Integer) ObjectUtil.defaultIfNull(itemArr.get(4), 0));
            futureHoldingDO.setLongHld((Integer) ObjectUtil.defaultIfNull(itemArr.get(5), 0));
            futureHoldingDO.setLongChg((Integer) ObjectUtil.defaultIfNull(itemArr.get(6), 0));
            futureHoldingDO.setShortHld((Integer) ObjectUtil.defaultIfNull(itemArr.get(7), 0));
            futureHoldingDO.setShortChg((Integer) ObjectUtil.defaultIfNull(itemArr.get(8), 0));

            tsFutureHoldingDOList.add(futureHoldingDO);
        }
        return tsFutureHoldingDOList;
    }

}

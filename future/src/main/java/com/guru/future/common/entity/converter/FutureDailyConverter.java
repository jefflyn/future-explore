package com.guru.future.common.entity.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.guru.future.common.utils.ObjectUtil;
import com.guru.future.domain.TsFutureDailyDO;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class FutureDailyConverter {

    private final static String[] DAILY_COLS = new String[]{"ts_code", "trade_date", "pre_close", "pre_settle", "open", "high", "low", "close", "settle", "change1", "change2", "vol", "amount", "oi", "oi_chg"};

    public static List<TsFutureDailyDO> toTsFutureDailyDO(String jsonResult) {
        List<TsFutureDailyDO> tsFutureDailyDOList = new ArrayList<>();
        if (Strings.isNullOrEmpty(jsonResult)) {
            return tsFutureDailyDOList;
        }
        JSONObject jsonObject = JSON.parseObject(jsonResult);
        JSONObject dataJson = (JSONObject) jsonObject.get("data");
        JSONArray fields = (JSONArray) dataJson.get("fields");
        for (int i = 0; i < DAILY_COLS.length; i++) {
            Assert.isTrue(DAILY_COLS[i].equals(fields.get(i)), "ts daily columns change， please check!");
        }
        JSONArray items = (JSONArray) dataJson.get("items");
        Iterator iterator = items.iterator();
        Date time = new Date();
        while (iterator.hasNext()) {
            JSONArray itemArr = (JSONArray) iterator.next();
            if (Boolean.TRUE.equals(ObjectUtil.checkNullValue(itemArr.get(4), itemArr.get(5), itemArr.get(7)))) {
                continue;
            }
            TsFutureDailyDO tsFutureDailyDO = new TsFutureDailyDO();
            tsFutureDailyDO.setTsCode((String) itemArr.get(0));
            tsFutureDailyDO.setTradeDate((String) itemArr.get(1));
            tsFutureDailyDO.setPreClose((BigDecimal) itemArr.get(2));
            tsFutureDailyDO.setPreSettle((BigDecimal) itemArr.get(3));
            tsFutureDailyDO.setOpen((BigDecimal) itemArr.get(4));
            tsFutureDailyDO.setHigh((BigDecimal) itemArr.get(5));
            tsFutureDailyDO.setLow((BigDecimal) itemArr.get(6));
            tsFutureDailyDO.setClose((BigDecimal) itemArr.get(7));
            tsFutureDailyDO.setSettle((BigDecimal) itemArr.get(8));
            tsFutureDailyDO.setCloseChange((BigDecimal) itemArr.get(9));
            tsFutureDailyDO.setSettleChange((BigDecimal) itemArr.get(10));
            tsFutureDailyDO.setDealVol((BigDecimal) itemArr.get(11));
            tsFutureDailyDO.setDealAmount((BigDecimal) itemArr.get(12));
            tsFutureDailyDO.setHoldVol((BigDecimal) itemArr.get(13));
            tsFutureDailyDO.setHoldChange((BigDecimal) itemArr.get(14));
            tsFutureDailyDO.setCreateTime(time);

            tsFutureDailyDOList.add(tsFutureDailyDO);
        }
        return tsFutureDailyDOList;
    }

}

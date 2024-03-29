package com.guru.future.common.entity.converter;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.guru.future.common.utils.NullUtil;
import com.guru.future.common.entity.dao.TsFutureDailyDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class FutureDailyConverter {

    private final static String[] DAILY_COLS = new String[]{"ts_code", "trade_date", "pre_close", "pre_settle", "open", "high", "low", "close", "settle", "change1", "change2", "vol", "amount", "oi", "oi_chg"};

    public static List<TsFutureDailyDO> toTsFutureDailyDO(String jsonResult) {
        List<TsFutureDailyDO> tsFutureDailyDOList = new ArrayList<>();
        if (Strings.isNullOrEmpty(jsonResult)) {
            log.warn("没有查到数据，处理完成！");
            return tsFutureDailyDOList;
        }
        JSONObject jsonObject = JSON.parseObject(jsonResult);
        JSONObject dataJson = (JSONObject) jsonObject.get("data");
        if (dataJson == null) {
            log.warn("没有查到data数据，处理完成！");
            return tsFutureDailyDOList;
        }
        JSONArray fields = (JSONArray) dataJson.get("fields");
        for (int i = 0; i < DAILY_COLS.length; i++) {
            Assert.isTrue(DAILY_COLS[i].equals(fields.get(i)), "ts daily columns change， please check!");
        }
        JSONArray items = (JSONArray) dataJson.get("items");
        Iterator iterator = items.iterator();
        Date time = new Date();
        while (iterator.hasNext()) {
            JSONArray itemArr = (JSONArray) iterator.next();
            if (Boolean.TRUE.equals(NullUtil.checkNullValue(itemArr.get(8)))) {
                log.warn("{} 数据不全，跳过... {}", itemArr.get(0), itemArr);
                continue;
            }
            BigDecimal open = (BigDecimal) ObjectUtil.defaultIfNull(itemArr.get(4), itemArr.get(8));
            BigDecimal high = (BigDecimal) ObjectUtil.defaultIfNull(itemArr.get(5), open);
            BigDecimal low = (BigDecimal) ObjectUtil.defaultIfNull(itemArr.get(6), high);
            TsFutureDailyDO tsFutureDailyDO = new TsFutureDailyDO();
            tsFutureDailyDO.setTsCode((String) itemArr.get(0));
            tsFutureDailyDO.setTradeDate((String) itemArr.get(1));
            tsFutureDailyDO.setPreClose((BigDecimal) itemArr.get(2));
            tsFutureDailyDO.setPreSettle((BigDecimal) ObjectUtil.defaultIfNull(itemArr.get(3), itemArr.get(8)));
            tsFutureDailyDO.setOpen(open);
            tsFutureDailyDO.setHigh(high);
            tsFutureDailyDO.setLow(low);
            tsFutureDailyDO.setClose((BigDecimal) ObjectUtil.defaultIfNull(itemArr.get(7), open));
            tsFutureDailyDO.setSettle((BigDecimal) itemArr.get(8));
            tsFutureDailyDO.setCloseChange((BigDecimal) ObjectUtil.defaultIfNull(itemArr.get(9), BigDecimal.ZERO));
            tsFutureDailyDO.setSettleChange((BigDecimal) ObjectUtil.defaultIfNull(itemArr.get(10), BigDecimal.ZERO));
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

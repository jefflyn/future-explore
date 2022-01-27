package com.guru.future.common.entity.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.guru.future.common.enums.ContractType;
import com.guru.future.domain.TsFutureContractDO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ContractBasicConverter {

    public static List<TsFutureContractDO> toTsFutureContractDO(String jsonResult) {
        List<TsFutureContractDO> tsFutureContractDOList = new ArrayList<>();
        if (Strings.isNullOrEmpty(jsonResult)) {
            return Collections.emptyList();
        }
        JSONObject jsonObject = JSON.parseObject(jsonResult);
        JSONObject dataJson = (JSONObject) jsonObject.get("data");
        JSONArray itemList = (JSONArray) dataJson.get("items");
        Iterator iterator = itemList.iterator();
        while (iterator.hasNext()) {
            JSONArray items = (JSONArray) iterator.next();
            TsFutureContractDO tsFutureContractDO = new TsFutureContractDO();
            tsFutureContractDO.setTsCode(String.valueOf(items.get(0)));
            tsFutureContractDO.setSymbol(String.valueOf(items.get(1)));
            tsFutureContractDO.setExchange(String.valueOf(items.get(2)));
            tsFutureContractDO.setName(String.valueOf(items.get(3)));
            tsFutureContractDO.setFutCode(String.valueOf(items.get(4)));
            if (tsFutureContractDO.getName().contains(ContractType.CONTINUE.getDesc())) {
                tsFutureContractDO.setType(ContractType.CONTINUE.getCode());
            } else if (tsFutureContractDO.getName().contains(ContractType.MAIN.getDesc())) {
                tsFutureContractDO.setType(ContractType.MAIN.getCode());
            } else {
                tsFutureContractDO.setType(ContractType.NORMAL.getCode());
            }
            tsFutureContractDOList.add(tsFutureContractDO);
        }
        return tsFutureContractDOList;
    }
}

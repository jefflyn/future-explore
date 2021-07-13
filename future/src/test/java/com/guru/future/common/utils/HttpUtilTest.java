package com.guru.future.common.utils;


import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.List;

public class HttpUtilTest {
    private String reqUrl = "http://hq.sinajs.cn/list=nf_SA2109,nf_OI2109";


    @Test
    public void testDoGet() {
        String result = HttpUtil.doGet(reqUrl);
        List<String> contractList = Splitter.on(";\n").splitToList(result);

        for (String contract : contractList) {
            if (Strings.isNullOrEmpty(contract)) {
                continue;
            }
            System.out.println(contract);
            System.out.println(JSON.toJSONString(ContractRealtimeDTO.convertFromHqList(SinaHqUtil.parse2List(contract))));
        }
    }

    @Test
    public void testDoPost() {
    }

    @Test
    public void testDoPostJson() {
    }
}
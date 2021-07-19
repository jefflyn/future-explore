package com.guru.future.biz.manager;

import com.google.common.base.Splitter;
import com.guru.future.common.utils.HttpUtil;
import com.guru.future.common.utils.SinaHqUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author j
 */
@Component
public class FutureSinaManager {
    private final static String SINA_HQ_URL = "http://hq.sinajs.cn/list=";

    public List<String> fetchContractInfo(List<String> codeList) {
        StringBuilder reqCodes = new StringBuilder();
        for (String code : codeList) {
            reqCodes.append(SinaHqUtil.convert2HqCode(code)).append(",");
        }
        String result = HttpUtil.doGet(SINA_HQ_URL + reqCodes);
        List<String> contractList = Splitter.on(";\n").splitToList(result);
        return contractList;
    }

}

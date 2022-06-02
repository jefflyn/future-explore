package com.guru.future.biz.manager.remote;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.guru.future.biz.manager.FutureBasicManager;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.utils.HttpUtil;
import com.guru.future.common.utils.SinaHqUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author j
 */
@Component
@Slf4j
public class FutureSinaManager {
    // http://w.sinajs.cn/?list=
    private final static String SINA_HQ_URL = "https://hq.sinajs.cn/list=";

    @Resource
    private FutureBasicManager futureBasicManager;

    public List<String> fetchContractInfo(List<String> codeList) {
        StringBuilder reqCodes = new StringBuilder();
        for (String code : codeList) {
            reqCodes.append(SinaHqUtil.convert2HqCode(code)).append(",");
        }
        String url = SINA_HQ_URL + reqCodes;
        Map<String, String> header = new HashMap<>();
        header.put("Referer", "https://finance.sina.com.cn");
        String result = HttpUtil.doGet(url, null, header);
//        var hq_str_nf_PP2209="聚丙烯2209,101459,8910.000,8967.000,8862.000,0.000,8882.000,8884.000,8882.000,0.000,8878.000,5,62,414914.000,348914,连,聚丙烯,2022-06-02,1,,,,,,,,,8911.924,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0"
//        log.info("url={}, result={}", url, JSON.toJSONString(result));
        List<String> contractList = Splitter.on(";\n").splitToList(result);
        return contractList;
    }

    public List<ContractRealtimeDTO> getAllRealtimeFromSina() {
        List<String> codeList = futureBasicManager.getAllCodes();
        if (CollectionUtils.isEmpty(codeList)) {
            return Lists.newArrayList();
        }
        return this.getRealtimeFromSina(codeList);
    }

    public List<ContractRealtimeDTO> getRealtimeFromSina(List<String> codeList) {
        List<String> contractList = fetchContractInfo(codeList);
        List<ContractRealtimeDTO> contractRealtimeDTOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(contractList)) {
            for (String contract : contractList) {
                if (Strings.isNullOrEmpty(contract)) {
                    continue;
                }
                ContractRealtimeDTO contractRealtimeDTO = ContractRealtimeDTO.convertFromHqList(SinaHqUtil.parse2List(contract));
                if (contractRealtimeDTO.getCode() == null) {
                    log.warn("data error, skip {}", contract);
                    continue;
                }
                contractRealtimeDTOList.add(contractRealtimeDTO);
                int randomNumber = RandomUtils.nextInt(1, 50000);
                if (contractRealtimeDTOList.size() == randomNumber) {
                    log.info("contractRealtimeDTOList.size()={}, randomNumber={}, contract={}",
                            contractRealtimeDTOList.size(), randomNumber, contract);
                }
            }
        }
        return contractRealtimeDTOList;
    }

}

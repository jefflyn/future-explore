package com.guru.future.biz.manager;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.utils.HttpUtil;
import com.guru.future.common.utils.SinaHqUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author j
 */
@Component
@Slf4j
public class FutureSinaManager {
    private final static String SINA_HQ_URL = "https://hq.sinajs.cn/list=";

    @Resource
    private FutureBasicManager futureBasicManager;

    public List<String> fetchContractInfo(List<String> codeList) {
        StringBuilder reqCodes = new StringBuilder();
        for (String code : codeList) {
            reqCodes.append(SinaHqUtil.convert2HqCode(code)).append(",");
        }
        String result = HttpUtil.doGet(SINA_HQ_URL + reqCodes);
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
//                    System.out.println(JSON.toJSONString();
                ContractRealtimeDTO contractRealtimeDTO = ContractRealtimeDTO.convertFromHqList(SinaHqUtil.parse2List(contract));
                if (contractRealtimeDTO.getCode() == null) {
                    log.warn("skip {}", contract);
                }
                contractRealtimeDTOList.add(contractRealtimeDTO);
                if (contractRealtimeDTOList.size() == RandomUtils.nextInt(1, 2000)) {
                    log.info(contract);
                }
            }
        }
        return contractRealtimeDTOList;
    }

}

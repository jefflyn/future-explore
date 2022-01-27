package com.guru.future.biz.service;

import com.guru.future.biz.manager.TsFutureBasicManager;
import com.guru.future.biz.manager.remote.TsFutureManager;
import com.guru.future.common.constants.TsFutureConstant;
import com.guru.future.common.entity.converter.ContractBasicConverter;
import com.guru.future.common.enums.Exchange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author j
 */
@Service
@Slf4j
public class FutureBasicService {
    @Resource
    private TsFutureManager tsFutureManager;

    @Resource
    private TsFutureBasicManager tsFutureBasicManager;

    public Boolean addFutureDailyFromTs() {
        for (Exchange exchange : Exchange.values()) {
            String result = tsFutureManager.getBasic(exchange.getCode(), TsFutureConstant.CONTRACT_TYPE_MAIN_CONT);
            tsFutureBasicManager.batchAddFutureBasic(ContractBasicConverter.toTsFutureContractDO(result));
        }
        return true;
    }


}

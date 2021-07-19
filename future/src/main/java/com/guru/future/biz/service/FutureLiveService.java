package com.guru.future.biz.service;

import com.guru.future.biz.manager.FutureLiveManager;
import com.guru.future.common.entity.converter.FutureLiveConverter;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.domain.FutureLiveDO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FutureLiveService {

    @Resource
    private FutureLiveManager futureLiveManager;

    @Async
    public void refreshLiveData(ContractRealtimeDTO contractRealtimeDTO) {
        FutureLiveDO futureLiveDO = FutureLiveConverter.convertFromContractRealtimeDTO(contractRealtimeDTO);
        futureLiveManager.upsertFutureLive(futureLiveDO);
    }
}

package com.guru.future.controller;

import com.guru.future.biz.handler.FutureTaskDispatcher;
import com.guru.future.biz.manager.ContractManager;
import com.guru.future.biz.manager.remote.FutureSinaManager;
import com.guru.future.biz.service.FutureLiveService;
import com.guru.future.common.entity.dto.ContractRealtimeDTO;
import com.guru.future.common.entity.vo.FutureOverviewVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/***
 * @author j
 * @date 2021/7/19 5:32 下午
 **/
@RestController
public class FutureLiveController {
    @Resource
    private FutureTaskDispatcher futureTaskDispatcher;

    @Resource
    private FutureSinaManager futureSinaManager;

    @Resource
    private ContractManager contractManager;

    @Resource
    private FutureLiveService futureLiveService;

    @GetMapping(value = "/future/live/start")
    public String start(@RequestParam(required = false) Boolean refresh) {
        try {
            futureTaskDispatcher.executePulling(refresh);
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return "success";
    }

    @GetMapping(value = "/future/live")
    public String live() {
        List<ContractRealtimeDTO> contractRealtimeDTOList = futureSinaManager.getRealtimeFromSina(contractManager.getContractCodes());
        // async live data
        futureLiveService.refreshLiveData(contractRealtimeDTOList, true);
        return "success";
    }

    @GetMapping(value = "/future/live/stop")
    public Boolean stop() {
        return futureTaskDispatcher.stopRunning();
    }

    @GetMapping(value = "/future/live/overview")
    public String liveOverview() {
        FutureOverviewVO overviewVO = futureLiveService.getMarketOverview();
        return overviewVO.toString();
    }

    @GetMapping(value = "/future/mail/overview")
    public String mailOverview() throws Exception {
        futureLiveService.sendMarketOverviewMail();
        return "success";
    }

    @GetMapping(value = "/future/hist/overview")
    public String reviewHistOverview() {
        String result = futureLiveService.showHistOverview();
        result = result.replace("| \n", "| ");
        return result;
    }
}

package com.guru.future.controller;

import com.guru.future.biz.service.gene.HoldingService;
import com.guru.future.common.utils.FutureDateUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/***
 * @author j
 * @date 2021/7/19 5:32 下午
 **/
@RestController
public class FutureHoldingController {
    @Resource
    private HoldingService holdingService;

    @GetMapping(value = "/future/holding")
    public String addHolding(@RequestParam(value = "codes") String codes,
                            @RequestParam(value = "start", required = false) String start,
                            @RequestParam(value = "end", required = false) String end) {
        String startDate = start == null ? FutureDateUtil.latestTradeDate(FutureDateUtil.TRADE_DATE_PATTERN_FLAT) : start;
        String endDate = end == null ? FutureDateUtil.latestTradeDate(FutureDateUtil.TRADE_DATE_PATTERN_FLAT) : end;
        holdingService.batchAddHolding(codes, startDate, endDate);
        return "success";
    }

}

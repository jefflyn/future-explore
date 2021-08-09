package com.guru.future.controller;

import com.guru.future.biz.service.FutureDailyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/***
 * @author j
 * @date 2021/7/19 5:32 下午
 **/
@RestController
public class FutureDailyController {
    @Resource
    private FutureDailyService futureDailyService;

    @GetMapping(value = "/future/daily/update")
    public String updateDaily() {
        futureDailyService.addTradeDaily();
        return "success";
    }


}
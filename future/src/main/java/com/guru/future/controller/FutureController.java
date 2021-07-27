package com.guru.future.controller;

import com.guru.future.biz.handler.FutureTaskDispatcher;
import com.guru.future.biz.service.FutureDailyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/***
 * @author j
 * @date 2021/7/19 5:32 下午
 **/
@RestController
public class FutureController {
    @Resource
    private FutureTaskDispatcher futureTaskDispatcher;
    @Resource
    private FutureDailyService futureDailyService;

    @GetMapping(value = "/future/live/start")
    public String start(@RequestParam(required = false) Boolean refresh) {
        try {
            futureTaskDispatcher.executePulling(refresh);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "success";
    }

    @GetMapping(value = "/future/live/stop")
    public Boolean stop() {
        return futureTaskDispatcher.stopRunning();
    }

    @GetMapping(value = "/future/daily/update")
    public String updateDaily() {
        futureDailyService.addTradeDaily();
        return "success";
    }
}

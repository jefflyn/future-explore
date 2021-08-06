package com.guru.future.controller;

import com.guru.future.biz.handler.FutureTaskDispatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author j
 */
@RestController
public class FutureBasicController {
    @Resource
    private FutureTaskDispatcher futureTaskDispatcher;

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
}

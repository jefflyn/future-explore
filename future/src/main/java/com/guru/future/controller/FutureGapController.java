package com.guru.future.controller;

import com.guru.future.biz.service.FutureGapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/***
 * @author j
 * @date 2021/7/19 5:32 下午
 **/
@RestController
public class FutureGapController {
    @Resource
    private FutureGapService futureGapService;

    @GetMapping(value = "/future/open/gap")
    public String openGap() throws InterruptedException {
        futureGapService.monitorOpenGap();
        return "success";
    }
}

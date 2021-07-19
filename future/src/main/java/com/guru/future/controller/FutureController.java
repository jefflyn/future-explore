package com.guru.future.controller;

import com.guru.future.biz.handler.FutureTaskDispatcher;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping(value = "/future/start")
    public void list() {
        futureTaskDispatcher.executePulling();
    }
}

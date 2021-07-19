package com.guru.future.controller;

import com.guru.future.biz.manager.FutureBasicManager;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/***
 * @author j
 * @date 2021/7/19 5:32 下午
 **/
@RestController
public class FutureController {
    @Resource
    private FutureBasicManager futureBasicManager;

}

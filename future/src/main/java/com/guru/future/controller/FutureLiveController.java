package com.guru.future.controller;

import com.guru.future.biz.service.FutureLiveService;
import com.guru.future.common.entity.vo.FutureOverviewVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/***
 * @author j
 * @date 2021/7/19 5:32 下午
 **/
@RestController
public class FutureLiveController {
    @Resource
    private FutureLiveService futureLiveService;

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
}

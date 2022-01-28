package com.guru.future.controller;

import com.google.common.base.Splitter;
import com.guru.future.biz.service.FutureDailyService;
import com.guru.future.biz.service.TsFutureDailyService;
import com.guru.future.common.entity.vo.PositionCollectVO;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/***
 * @author j
 * @date 2021/7/19 5:32 下午
 **/
@RestController
public class FutureDailyController {
    @Resource
    private FutureDailyService futureDailyService;

    @Resource
    private TsFutureDailyService tsFutureDailyService;

    @GetMapping(value = "/future/daily/add")
    public String addTsDaily(@RequestParam(value = "tsCodes", required = false) String tsCodes,
                             @RequestParam(value = "start", required = false, defaultValue = "") String start,
                             @RequestParam(value = "end", required = false, defaultValue = "") String end) {
        tsFutureDailyService.batchAddDaily(tsCodes, start, end);
        return "success";
    }

    @GetMapping(value = "/future/daily/update")
    public String updateDaily() {
        futureDailyService.addTradeDaily();
        return "success";
    }

    @CrossOrigin
    @GetMapping(value = "/future/daily/position/list")
    public PositionCollectVO listPosition(@RequestParam(value = "tradeDate", required = false) String tradeDate,
                                          @RequestParam(value = "codes", required = false) String codes) {
        List<String> codeList = new ArrayList<>();
        if (Strings.isNotBlank(codes)) {
            Iterator<String> codeIte = Splitter.on(",").trimResults().omitEmptyStrings().split(codes).iterator();
            while (codeIte.hasNext()) {
                codeList.add(codeIte.next());
            }
        }
        return futureDailyService.getCurrentPositionList(tradeDate, codeList);
    }
}

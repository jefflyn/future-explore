package com.guru.future.controller;

import cn.hutool.core.text.finder.CharFinder;
import cn.hutool.core.text.split.SplitIter;
import com.guru.future.biz.service.gene.WaveService;
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
public class FutureWaveController {
    @Resource
    private WaveService waveService;

    @GetMapping(value = "/future/daily/wave")
    public String waveDaily(@RequestParam(value = "tsCodes") String tsCodes,
                            @RequestParam(value = "start", required = false) String start,
                            @RequestParam(value = "end", required = false) String end) {
        SplitIter splitIter = new SplitIter(tsCodes, new CharFinder(','), 1000, true);
        List<String> deviceNoList = splitIter.toList(true);
        waveService.startWaveProcess(deviceNoList, start, end);
        return "success";
    }

}

package com.guru.future.controller;

import com.guru.future.domain.FutureBasicsDO;
import com.guru.future.mapper.FutureBasicsDAO;
import com.guru.future.mapper.FutureRealtimeDAO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class FutureBasicController {

    @Resource
    private FutureBasicsDAO futureBasicsDAO;
    @Resource
    private FutureRealtimeDAO futureRealtimeDAO;

    @GetMapping(value = "/future/basic/list")
    public FutureBasicsDO list() {
        return futureBasicsDAO.selectByPrimaryKey("OI");
    }

    @GetMapping(value = "/future/realtime")
    public List realtime() {
        return futureRealtimeDAO.selectAll();
    }
}

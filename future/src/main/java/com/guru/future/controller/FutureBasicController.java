package com.guru.future.controller;

import com.guru.future.biz.manager.TsFutureBasicManager;
import com.guru.future.biz.service.FutureBasicService;
import com.guru.future.common.enums.ContractType;
import org.apache.logging.log4j.util.Strings;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/***
 * @author j
 * @date 2021/7/19 5:32 下午
 **/
@RestController
public class FutureBasicController {
    @Resource
    private FutureBasicService futureBasicService;

    @Resource
    private TsFutureBasicManager tsFutureBasicManager;

    @Resource
    private RedissonClient redissonClient;

    @GetMapping(value = "/test/cache")
    public Long testCache() {
        RMapCache<String, Long> idMap = redissonClient.getMapCache("test");
        idMap.expire(30, TimeUnit.SECONDS);
        Long id = idMap.addAndGet("incr", 1L);
        return id;
    }

    @GetMapping(value = "/contract/basic/add")
    public String addContractBasic() {
        futureBasicService.addFutureDailyFromTs();
        return "success";
    }

    @GetMapping(value = "/tsCodes")
    public String listTsCode() {
        List<String> tsCodes = tsFutureBasicManager.getTsCodeByType(ContractType.CONTINUE.getCode());
        return Strings.join(tsCodes, ',');
    }
}

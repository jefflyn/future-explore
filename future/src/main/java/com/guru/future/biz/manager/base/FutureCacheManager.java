package com.guru.future.biz.manager.base;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class FutureCacheManager {
    @Resource
    private RedissonClient redissonClient;

    public void put(String key, Object value, long expireTime, TimeUnit timeUnit) {
        try {
            redissonClient.getBucket(key).set(value, expireTime, timeUnit);
        } catch (Exception e) {
            log.error("set redis cache fail, error={}", e);
        }
    }

    public Object get(String key) {
        try {
            return redissonClient.getBucket(key).get();
        } catch (Exception e) {
            log.error("get redis cache fail, error={}", e);
        }
        return null;
    }
}

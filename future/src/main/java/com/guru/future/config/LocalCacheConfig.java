package com.guru.future.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * 本地缓存
 * 1.注意不要存储大对象，防止OOM
 * 2.适合缓存不常变化且值数量较少的数据
 * 3.必须使用cacheName做数据存储隔离
 */
@Configuration
@EnableCaching
public class LocalCacheConfig {

    /**
     * 默认本地缓存管理器
     *
     * @return
     */
    @Primary
    @Bean("hour1CacheManager")
    public CacheManager hour1CacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .recordStats()
                .initialCapacity(16)
                .maximumSize(64)
                .expireAfterWrite(1, TimeUnit.HOURS)
        );
        return cacheManager;
    }

    @Bean("minute3CacheManager")
    public CacheManager minute3CacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .recordStats()
                .initialCapacity(16)
                .maximumSize(64)
                .expireAfterWrite(3, TimeUnit.MINUTES)
        );
        return cacheManager;
    }

}

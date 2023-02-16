package com.guru.future.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@Slf4j
public class BizAsyncConfigurer implements AsyncConfigurer {

    @Bean("bizAsyncTaskExecutor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        // 当前线程数
        threadPool.setCorePoolSize(5);
        // 最大线程数
        threadPool.setMaxPoolSize(10);
        // 线程池所使用的缓冲队列
        threadPool.setQueueCapacity(100);
        // 等待任务在关机时完成--表明等待所有线程执行完
        threadPool.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间 （默认为0，此时立即停止），并没等待xx秒后强制停止
        threadPool.setAwaitTerminationSeconds(60 * 15);
        // 线程名称前缀
        threadPool.setThreadNamePrefix("BizAsync-");
        // 拒绝策略
        threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 允许的空闲时间,当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        threadPool.setKeepAliveSeconds(300);
        threadPool.initialize(); // 初始化
        log.info("开启异步任务线程池");
        return threadPool;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            log.info("handleUncaughtException Method name - " + method.getName(), throwable);
            for (Object param : objects) {
                log.info("handleUncaughtException Parameter value - " + param);
            }
        };
    }
}
package com.guru.future.task;

import com.guru.future.common.cache.LiveDataCache;
import lombok.extern.slf4j.Slf4j;

/**
 * @author j
 * @date 2021/7/29 9:17 下午
 **/
@Slf4j
public class LiveSnapshotRefreshTask implements Runnable {
    @Override
    public void run() {
        LiveDataCache.snapshotRefreshable();
    }
}


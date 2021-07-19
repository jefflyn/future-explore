package com.guru.future.biz.handler;

import com.guru.future.TestBase;
import org.testng.annotations.Test;

import javax.annotation.Resource;

public class FutureTaskDispatcherTest extends TestBase {

    @Resource
    private FutureTaskDispatcher futureTaskDispatcher;

    @Test
    public void testExecutePulling() {
        futureTaskDispatcher.executePulling();
    }
}
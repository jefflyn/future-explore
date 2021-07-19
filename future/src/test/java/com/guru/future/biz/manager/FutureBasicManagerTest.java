package com.guru.future.biz.manager;


import com.guru.future.TestBase;
import com.guru.future.domain.FutureBasicDO;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.List;

public class FutureBasicManagerTest extends TestBase {

    @Resource
    private FutureBasicManager futureBasicManager;

    @Test
    public void testGetAllCodes() {
        System.out.println(futureBasicManager.getAllCodes());
    }

    @Test
    public void testGetAll() {
        List<FutureBasicDO> result = futureBasicManager.getAll();
        System.out.println(result);
    }
}
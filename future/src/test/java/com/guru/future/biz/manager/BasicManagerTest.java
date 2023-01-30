package com.guru.future.biz.manager;


import com.guru.future.TestBase;
import com.guru.future.common.entity.dao.FutureBasicDO;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.List;

public class BasicManagerTest extends TestBase {

    @Resource
    private BasicManager basicManager;

    @Test
    public void testGetAllCodes() {
    }

    @Test
    public void testGetAll() {
        List<FutureBasicDO> result = basicManager.getAll();
        System.out.println(result);
    }
}
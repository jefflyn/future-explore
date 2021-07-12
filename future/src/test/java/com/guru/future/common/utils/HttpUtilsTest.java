package com.guru.future.common.utils;


import com.google.common.base.Splitter;
import org.junit.jupiter.api.Test;

import java.util.List;

public class HttpUtilsTest {
    private String reqUrl = "http://hq.sinajs.cn/list=nf_SA2109,nf_OI2109";


    @Test
    public void testDoGet() {

    }

    @Test
    public void testTestDoGet() {
        String result = HttpUtils.doGet(reqUrl);
        List<String> contractList = Splitter.on(";/n").splitToList(result);

        for (String contract : contractList) {
            System.out.println(contract);
        }
    }

    @Test
    public void testDoPost() {
    }

    @Test
    public void testTestDoPost() {
    }

    @Test
    public void testDoPostJson() {
    }
}
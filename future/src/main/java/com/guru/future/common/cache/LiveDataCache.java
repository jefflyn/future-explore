package com.guru.future.common.cache;

import com.guru.future.common.entity.vo.FutureLiveVO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LiveDataCache {
    public static Map<String, FutureLiveVO> top10Snapshot = new HashMap<>(20);
    public static List<FutureLiveVO> highTop10 = new ArrayList<>(10);
    public static List<FutureLiveVO> lowTop10 = new ArrayList<>(10);


}

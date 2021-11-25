package com.guru.future.common.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.guru.future.common.entity.vo.FutureLiveVO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LiveDataCache {
    public final static String SYMBOL_UP = "↑";
    public final static String SYMBOL_DOWN = "↓";
    private static Cache<String, FutureLiveVO> top10Snapshot = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .maximumSize(20)
            .build();
    //    private static Map<String, FutureLiveVO> top10Snapshot = new HashMap<>(20);
    private static List<FutureLiveVO> highTop10 = new ArrayList<>(10);
    private static List<FutureLiveVO> lowTop10 = new ArrayList<>(10);

    private LiveDataCache() {
    }

    public static void setHighTop10(List<FutureLiveVO> futureLiveVOList) {
        highTop10.clear();
        for (int i = 0; i < futureLiveVOList.size(); i++) {
            FutureLiveVO highTopVo = futureLiveVOList.get(i);
            highTopVo.setSortNo(i + 1);
            highTopVo.setHighTop(true);
            FutureLiveVO lastVo = top10Snapshot.getIfPresent(highTopVo.getName());
            if (lastVo != null && !highTopVo.getSortNo().equals(lastVo.getSortNo())) {
                highTopVo.setDirection(highTopVo.getSortNo().compareTo(lastVo.getSortNo()) < 0 ? SYMBOL_UP : SYMBOL_DOWN);
            } else {
                top10Snapshot.put(highTopVo.getName(), highTopVo);
            }
            highTop10.add(highTopVo);
        }
    }

    public static void setLowTop10(List<FutureLiveVO> futureLiveVOList) {
        lowTop10.clear();
        for (int i = 0; i < futureLiveVOList.size(); i++) {
            FutureLiveVO lowTopVo = futureLiveVOList.get(i);
            lowTopVo.setSortNo(i + 1);
            lowTopVo.setHighTop(false);
            FutureLiveVO lastVo = top10Snapshot.getIfPresent(lowTopVo.getName());
            if (lastVo != null && !lowTopVo.getSortNo().equals(lastVo.getSortNo())) {
                lowTopVo.setDirection(lowTopVo.getSortNo().compareTo(lastVo.getSortNo()) < 0 ? SYMBOL_UP : SYMBOL_DOWN);
            } else {
                top10Snapshot.put(lowTopVo.getName(), lowTopVo);
            }
            lowTop10.add(lowTopVo);
        }
    }

    public static List<FutureLiveVO> getHighTop10() {
        return highTop10;
    }

    public static List<FutureLiveVO> getLowTop10() {
        return lowTop10;
    }
}

package com.guru.future.common.cache;

import com.guru.future.common.entity.vo.FutureLiveVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveDataCache {
    public final static String SYMBOL_UP = "↑";
    public final static String SYMBOL_DOWN = "↓";

    private static Boolean refreshSnapshot = true;
    private static Map<String, FutureLiveVO> top10Snapshot = new HashMap<>(20);
    private static List<FutureLiveVO> highTop10 = new ArrayList<>(10);
    private static List<FutureLiveVO> lowTop10 = new ArrayList<>(10);

    public static void setTop10Snapshot(List<FutureLiveVO> highTop10List, List<FutureLiveVO> lowTop10List) {
        if (refreshSnapshot) {
            for (FutureLiveVO highTop : highTop10List) {
                top10Snapshot.put(highTop.getName(), highTop);
            }
            for (FutureLiveVO lowTop : lowTop10List) {
                top10Snapshot.put(lowTop.getName(), lowTop);
            }
            refreshSnapshot = false;
        }
    }

    public static void snapshotRefreshable() {
        synchronized (LiveDataCache.class) {
            refreshSnapshot = true;
        }
    }

    public static void setHighTop10(List<FutureLiveVO> futureLiveVOList) {
        highTop10.clear();
        for (int i = 0; i < futureLiveVOList.size(); i++) {
            FutureLiveVO highTopVo = futureLiveVOList.get(i);
            highTopVo.setSortNo(i + 1);
            FutureLiveVO lastVo = top10Snapshot.get(highTopVo.getName());
            if (lastVo != null && highTopVo.getSortNo() != lastVo.getSortNo()) {
                highTopVo.setDirection(highTopVo.getSortNo().compareTo(lastVo.getSortNo()) < 0 ? SYMBOL_UP : SYMBOL_DOWN);
            }
        }
        highTop10.addAll(futureLiveVOList);
    }

    public static void setLowTop10(List<FutureLiveVO> futureLiveVOList) {
        lowTop10.clear();
        for (int i = 0; i < futureLiveVOList.size(); i++) {
            FutureLiveVO lowTopVo = futureLiveVOList.get(i);
            lowTopVo.setSortNo(i + 1);
            FutureLiveVO lastVo = top10Snapshot.get(lowTopVo.getName());
            if (lastVo != null && lowTopVo.getSortNo() != lastVo.getSortNo()) {
                lowTopVo.setDirection(lowTopVo.getSortNo().compareTo(lastVo.getSortNo()) < 0 ? SYMBOL_UP : SYMBOL_DOWN);
            }
        }
        lowTop10.addAll(futureLiveVOList);
    }

    public static List<FutureLiveVO> getHighTop10() {
        return highTop10;
    }

    public static List<FutureLiveVO> getLowTop10() {
        return lowTop10;
    }
}

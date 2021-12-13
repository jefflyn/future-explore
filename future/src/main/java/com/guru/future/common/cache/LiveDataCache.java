package com.guru.future.common.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.guru.future.common.entity.vo.FutureLiveVO;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LiveDataCache {
    public static final String SYMBOL_UP = "↑";
    public static final String SYMBOL_DOWN = "↓";
    private static Cache<String, FutureLiveVO> top10Snapshot = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .maximumSize(20)
            .build();
    private static List<FutureLiveVO> positionHighTop10 = new ArrayList<>(10);
    private static List<FutureLiveVO> positionLowTop10 = new ArrayList<>(10);
    private static List<FutureLiveVO> changeHighTop10 = new ArrayList<>(10);
    private static List<FutureLiveVO> changeLowTop10 = new ArrayList<>(10);

    private LiveDataCache() {
    }

    public static void setPositionHighTop10(List<FutureLiveVO> futureLiveVOList) {
        positionHighTop10.clear();
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
            positionHighTop10.add(highTopVo);
        }
    }

    public static void setPositionLowTop10(List<FutureLiveVO> futureLiveVOList) {
        positionLowTop10.clear();
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
            positionLowTop10.add(lowTopVo);
        }
    }

    public static List<FutureLiveVO> getPositionHighTop10() {
        return positionHighTop10;
    }

    public static List<FutureLiveVO> getPositionLowTop10() {
        return positionLowTop10;
    }

    public static void setChangeHighTop10(List<FutureLiveVO> changeHighTop10List) {
        changeHighTop10.clear();
        for (int i = 0; i < changeHighTop10List.size(); i++) {
            FutureLiveVO highTopVo = changeHighTop10List.get(i);
            FutureLiveVO newVo = new FutureLiveVO();
            BeanUtils.copyProperties(highTopVo, newVo);
            newVo.setSortNo(i + 1);
            newVo.setHighTop(true);
            newVo.setDirection("");
            changeHighTop10.add(newVo);
        }
    }

    public static List<FutureLiveVO> getChangeHighTop10() {
        return changeHighTop10;
    }

    public static void setChangeLowTop10(List<FutureLiveVO> changeLowTop10List) {
        changeLowTop10.clear();
        for (int i = 0; i < changeLowTop10List.size(); i++) {
            FutureLiveVO lowTopVo = changeLowTop10List.get(i);
            FutureLiveVO newVo = new FutureLiveVO();
            BeanUtils.copyProperties(lowTopVo, newVo);
            newVo.setSortNo(i + 1);
            newVo.setHighTop(false);
            newVo.setDirection("");
            changeLowTop10.add(newVo);
        }
    }

    public static List<FutureLiveVO> getChangeLowTop10() {
        return changeLowTop10;
    }
}

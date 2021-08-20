package com.guru.future.common.utils;

import org.apache.commons.lang3.ObjectUtils;
import org.javatuples.Pair;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PriceFlashCache {

    public static Map<String, LinkedList<BigDecimal>> PRICE_QUEUE = new HashMap<>(50);

    public static void rPush(String key, BigDecimal price) {
        LinkedList<BigDecimal> list = ObjectUtils.defaultIfNull(PRICE_QUEUE.get(key), new LinkedList());
        if (price.compareTo(ObjectUtils.defaultIfNull(peekLast(key), BigDecimal.ZERO)) == 0) {
            return;
        }
        list.add(price);
        PRICE_QUEUE.put(key, list);
    }

    public static void delete(String key) {
        PRICE_QUEUE.put(key, new LinkedList());
    }

    public static List get(String key) {
        return PRICE_QUEUE.get(key);
    }

    public static BigDecimal lPop(String key) {
        LinkedList<BigDecimal> list = ObjectUtils.defaultIfNull(PRICE_QUEUE.get(key), new LinkedList());
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.pop();
    }

    public static BigDecimal peekFirst(String key) {
        LinkedList<BigDecimal> list = ObjectUtils.defaultIfNull(PRICE_QUEUE.get(key), new LinkedList());
        return list.peekFirst();
    }

    public static BigDecimal peekLast(String key) {
        LinkedList<BigDecimal> list = ObjectUtils.defaultIfNull(PRICE_QUEUE.get(key), new LinkedList());
        return list.peekLast();
    }

    /**
     * Returns a view of the portion of this list between the specified fromIndex, inclusive, and toIndex, exclusive.
     *
     * @param key
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static List<BigDecimal> subList(String key, int fromIndex, int toIndex) {
        LinkedList<BigDecimal> list = ObjectUtils.defaultIfNull(PRICE_QUEUE.get(key), new LinkedList());
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        return list.subList(fromIndex, toIndex);
    }

    /**
     * 列表获取最小和最大值
     *
     * @param key
     * @param fromIndex
     * @param toIndex
     * @return min, max
     */
    public static Pair<BigDecimal, BigDecimal> getMinAndMaxFromList(String key, int fromIndex, int toIndex) {
        List<BigDecimal> list = subList(key, fromIndex, toIndex);
        if (CollectionUtils.isEmpty(list)) {
            return Pair.with(null, null);
        }
        return Pair.with(Collections.min(list), Collections.max(list));
    }

    /**
     * 列表获取最小和最大值
     *
     * @param key
     * @return min, max
     */
    public static Pair<BigDecimal, BigDecimal> getMinAndMaxFromList(String key) {
        LinkedList<BigDecimal> list = ObjectUtils.defaultIfNull(PRICE_QUEUE.get(key), new LinkedList());
        if (CollectionUtils.isEmpty(list)) {
            return Pair.with(null, null);
        }
        return Pair.with(Collections.min(list), Collections.max(list));
    }

    public static int length(String key) {
        LinkedList list = ObjectUtils.defaultIfNull(PRICE_QUEUE.get(key), new LinkedList());
        return list.size();
    }

    public static int index(String key, BigDecimal price) {
        LinkedList list = ObjectUtils.defaultIfNull(PRICE_QUEUE.get(key), new LinkedList());
        return list.indexOf(price);
    }
}

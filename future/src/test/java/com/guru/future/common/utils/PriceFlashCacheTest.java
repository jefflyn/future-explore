package com.guru.future.common.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PriceFlashCacheTest {

    @Test
    void rPush() {
        System.out.println(PriceFlashCache.lPop("A"));
        System.out.println(PriceFlashCache.length("A"));
        System.out.println(PriceFlashCache.peekFirst("A"));
        System.out.println(PriceFlashCache.subList("A", 0, 0));
        System.out.println(PriceFlashCache.getMinAndMaxFromList("A", 1, 12));
        PriceFlashCache.rPush("A", BigDecimal.valueOf(1));
        PriceFlashCache.rPush("A", BigDecimal.valueOf(2));
        PriceFlashCache.rPush("A", BigDecimal.valueOf(3));
        System.out.println(PriceFlashCache.PRICE_QUEUE);
        System.out.println(PriceFlashCache.peekFirst("A"));
        System.out.println(PriceFlashCache.lPop("A"));
        System.out.println(PriceFlashCache.PRICE_QUEUE);
        System.out.println(PriceFlashCache.lPop("A"));
        System.out.println(PriceFlashCache.PRICE_QUEUE);
    }

    @Test
    void lPop() {
    }

    @Test
    void peekFirst() {
    }

    @Test
    void subList() {
    }

    @Test
    void getMinAndMaxFromList() {
    }

    @Test
    void length() {
    }
}
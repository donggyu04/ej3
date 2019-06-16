package com.example.item6;

import org.junit.Test;

public class AutoBoxingTest {
    @Test
    public void testSumOfInteger() {
        long sum = 0L;      // primitive long type

        long startTime = System.nanoTime();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            sum += i;
        }
        long endTime = System.nanoTime();

        System.out.println(endTime - startTime);
    }

    @Test
    public void testSumOfIntegerWithAutoBoxing() {
        Long sum = 0L;      // wrapped primitive long type

        long startTime = System.nanoTime();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            sum += i;
        }
        long endTime = System.nanoTime();

        System.out.println(endTime - startTime);
    }
}

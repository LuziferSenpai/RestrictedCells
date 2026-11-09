package com.restrictedcells.util;

public final class SaturatedMath {

    private SaturatedMath() {}

    public static long multiply(long first, long second) {
        try {
            return Math.multiplyExact(first, second);
        } catch (ArithmeticException exception) {
            return (first < 0) == (second < 0) ? Long.MAX_VALUE : Long.MIN_VALUE;
        }
    }

    public static long add(long first, long second) {
        try {
            return Math.addExact(first, second);
        } catch (ArithmeticException exception) {
            return first < 0 ? Long.MIN_VALUE : Long.MAX_VALUE;
        }
    }
}

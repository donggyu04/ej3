package com.example.item6;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.function.Function;
import java.util.regex.Pattern;

public class CachingPatternTest {
    @Test
    public void testRomanNumeralsWithoutCaching() {
        Function<String, Boolean> isRomanNumerals = s -> s.matches("^(?=[MDCLXVI])M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
        long startTime = System.nanoTime();
        assertTrue(isRomanNumerals.apply("LXXXVIII"));
        long endTime = System.nanoTime();

        System.out.println(endTime - startTime);
    }

    @Test
    public void testRomanNumeralsWithCaching() {
        final Pattern ROMAN = Pattern.compile("^(?=[MDCLXVI])M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
        Function<String, Boolean> isRomanNumerals = s -> ROMAN.matcher(s).matches();

        long startTime = System.nanoTime();
        assertTrue(isRomanNumerals.apply("LXXXVIII"));
        long endTime = System.nanoTime();

        System.out.println(endTime - startTime);
    }
}

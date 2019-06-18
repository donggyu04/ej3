package com.example.item6;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class StringInternTest {
    @Test
    public void testStringEquals() {
        String platform = "platform";

        assertNotSame(platform, new String("platform"));
        assertSame(platform, new String("platform").intern());
    }
}

package io.github.toolkit.cache.util;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2025/4/10
 */
public class IOUtilsTest {

    @Test
    public void test() {
        System.out.println("王".getBytes(StandardCharsets.UTF_8).length);
        System.out.println("劉".getBytes(StandardCharsets.UTF_8).length);
        System.out.println("劉".toCharArray().length);
        System.out.println("劉".length());
    }

}
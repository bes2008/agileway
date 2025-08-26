package com.jn.agileway.httpclient.test.utils;

import com.jn.agileway.httpclient.util.HttpClientUtils;
import org.junit.jupiter.api.Test;

public class HttpClientUtilsTests {
    @Test
    public void test() {
        for (int i = 0; i < 100; i++) {
            System.out.println(HttpClientUtils.generateMultipartBoundary());
        }

    }
}

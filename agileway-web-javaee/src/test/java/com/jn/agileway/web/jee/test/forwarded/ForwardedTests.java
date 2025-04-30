package com.jn.agileway.web.jee.test.forwarded;

import com.jn.agileway.web.jee.test.mock.MockHttpRequest;
import com.jn.agileway.web.jee.test.mock.MockHttpServletRequest;
import com.jn.agileway.web.servlet.forwarded.OriginalRequest;
import com.jn.agileway.web.servlet.forwarded.OriginalRequestParser;
import org.junit.Test;

public class ForwardedTests {
    @Test
    public void testForwardedHeader() {
        MockHttpRequest mockRequest = new MockHttpRequest();
        mockRequest.addHeader("Forwarded", "for=192.0.2.60;proto=https;host=example.com:8443");
        mockRequest.setScheme("http");
        mockRequest.setServerName("localhost");
        mockRequest.setServerPort(8080);
        mockRequest.setContextPath("/app");

        OriginalRequestParser parser = new OriginalRequestParser(true, true);
        OriginalRequest originalRequest = parser.parse(new MockHttpServletRequest(mockRequest));
        String uri = originalRequest.getRequestBaseUrl(false);
        System.out.println(uri);

        uri = originalRequest.getRequestBaseUrl(true);
        System.out.println(uri);

        mockRequest.addHeader("X-Forwarded-Proto", "wss");
        mockRequest.addHeader("X-Forwarded-Path", "/testapp");
        originalRequest = parser.parse(new MockHttpServletRequest(mockRequest));
        uri = originalRequest.getRequestBaseUrl(true);
        System.out.println(uri);

    }

    @Test
    public void testForwardedForHeaders() {
        MockHttpRequest mockRequest = new MockHttpRequest();
        mockRequest.addHeader("X-Forwarded-For", "192.0.2.43, 198.51.100.2");
        mockRequest.addHeader("X-Forwarded-Proto", "wss, https, http");
        mockRequest.addHeader("X-Forwarded-Path", "/testapp");
        mockRequest.addHeader("X-Forwarded-Host", "example.com:8443");
        mockRequest.setScheme("http");
        mockRequest.setServerName("localhost");
        mockRequest.setServerPort(8080);
        mockRequest.setContextPath("/app");

        OriginalRequestParser parser = new OriginalRequestParser(true, true);
        OriginalRequest originalRequest = parser.parse(new MockHttpServletRequest(mockRequest));
        String uri = originalRequest.getRequestBaseUrl(false);
        System.out.println(uri);

    }
}

package com.jn.agileway.web.servlet;

import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpRequestFactory;

import javax.servlet.http.HttpServletRequest;

public class ServletHttpRequestFactory implements HttpRequestFactory<HttpServletRequest> {

    public static final ServletHttpRequestFactory INSTANCE = new ServletHttpRequestFactory();

    @Override
    public HttpRequest get(HttpServletRequest request) {
        return new ServletHttpRequest(request);
    }
}

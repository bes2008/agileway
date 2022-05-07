package com.jn.agileway.web.servlet;

import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpRequestFactory;

import javax.servlet.http.HttpServletRequest;

public class ServletHttpRequestFactory implements HttpRequestFactory<HttpServletRequest> {
    @Override
    public HttpRequest get(HttpServletRequest request) {
        return null;
    }
}

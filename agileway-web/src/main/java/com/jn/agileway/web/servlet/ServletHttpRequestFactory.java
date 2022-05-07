package com.jn.agileway.web.servlet;

import com.jn.agileway.web.rr.HttpRequest;
import com.jn.agileway.web.rr.HttpRequestFactory;

import javax.servlet.http.HttpServletRequest;

public class ServletHttpRequestFactory implements HttpRequestFactory<HttpServletRequest> {
    @Override
    public HttpRequest get(HttpServletRequest request) {
        return null;
    }
}

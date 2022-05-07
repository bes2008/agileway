package com.jn.agileway.web.servlet;

import com.jn.agileway.web.request.HttpRequest;
import com.jn.agileway.web.request.HttpRequestFactory;

import javax.servlet.http.HttpServletRequest;

public class ServletHttpRequestFactory implements HttpRequestFactory<HttpServletRequest> {
    @Override
    public HttpRequest get(HttpServletRequest request) {
        return null;
    }
}

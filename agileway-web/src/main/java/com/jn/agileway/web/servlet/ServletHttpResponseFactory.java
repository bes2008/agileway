package com.jn.agileway.web.servlet;

import com.jn.agileway.web.request.HttpResponse;
import com.jn.agileway.web.request.HttpResponseFactory;

import javax.servlet.http.HttpServletResponse;

public class ServletHttpResponseFactory implements HttpResponseFactory<HttpServletResponse> {
    @Override
    public HttpResponse get(HttpServletResponse response) {
        return null;
    }
}

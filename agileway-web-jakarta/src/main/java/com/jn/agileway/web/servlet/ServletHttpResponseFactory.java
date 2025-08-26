package com.jn.agileway.web.servlet;

import com.jn.agileway.http.rr.HttpResponse;
import com.jn.agileway.http.rr.HttpResponseFactory;

import jakarta.servlet.http.HttpServletResponse;

public class ServletHttpResponseFactory implements HttpResponseFactory<HttpServletResponse> {

    public static final ServletHttpResponseFactory INSTANCE = new ServletHttpResponseFactory();

    @Override
    public HttpResponse get(HttpServletResponse response) {
        return new ServletHttpResponse(response);
    }
}

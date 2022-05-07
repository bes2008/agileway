package com.jn.agileway.web.servlet;


import com.jn.agileway.http.rr.HttpResponse;

import javax.servlet.http.HttpServletResponse;

public class ServletHttpResponse implements HttpResponse<HttpServletResponse> {
    private HttpServletResponse delegate;
    @Override
    public HttpServletResponse getDelegate() {
        return delegate;
    }

    @Override
    public void addHeader(String name, String value) {
        delegate.addHeader(name,value);
    }
}

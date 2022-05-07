package com.jn.agileway.web.servlet;


import com.jn.agileway.http.rr.HttpRequest;

import javax.servlet.http.HttpServletRequest;

public class ServletHttpRequest implements HttpRequest<HttpServletRequest> {
    private HttpServletRequest delegate;
    @Override
    public HttpServletRequest getDelegate() {
        return delegate;
    }
}

package com.jn.agileway.web.servlet;


import com.jn.agileway.http.rr.HttpResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public class ServletHttpResponse implements HttpResponse<HttpServletResponse> {
    private HttpServletResponse delegate;

    public ServletHttpResponse(HttpServletResponse response){
        this.delegate = response;
    }
    @Override
    public HttpServletResponse getContainerResponse() {
        return delegate;
    }

    @Override
    public void addHeader(String name, String value) {
        delegate.addHeader(name,value);
    }

    @Override
    public String getHeader(String name) {
        return delegate.getHeader(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return delegate.getHeaderNames();
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return delegate.getHeaders(name);
    }

    @Override
    public int getStatusCode() {
        return delegate.getStatus();
    }
}

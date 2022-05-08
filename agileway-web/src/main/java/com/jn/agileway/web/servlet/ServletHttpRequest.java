package com.jn.agileway.web.servlet;


import com.jn.agileway.http.rr.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Locale;

public class ServletHttpRequest implements HttpRequest<HttpServletRequest> {
    private HttpServletRequest delegate;

    public ServletHttpRequest(HttpServletRequest delegate) {
        this.delegate = delegate;
    }

    @Override
    public HttpServletRequest getDelegate() {
        return delegate;
    }

    @Override
    public String getRemoteHost() {
        return delegate.getRemoteHost();
    }

    @Override
    public String getMethod() {
        return delegate.getMethod();
    }

    @Override
    public String getRequestURI() {
        return delegate.getRequestURI();
    }

    @Override
    public String getHeader(String name) {
        return delegate.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return delegate.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return delegate.getHeaderNames();
    }

    @Override
    public Object getAttribute(String name) {
        return delegate.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        delegate.setAttribute(name, value);
    }

    @Override
    public Locale getLocale() {
        return delegate.getLocale();
    }

    @Override
    public String getRequestURL() {
        return delegate.getRequestURL().toString();
    }
}

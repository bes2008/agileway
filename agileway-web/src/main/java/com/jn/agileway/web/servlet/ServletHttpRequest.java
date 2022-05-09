package com.jn.agileway.web.servlet;


import com.jn.agileway.http.rr.HttpRequest;
import com.jn.langx.util.collection.Pipeline;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Locale;

public class ServletHttpRequest implements HttpRequest<HttpServletRequest> {
    private HttpServletRequest delegate;

    public ServletHttpRequest(HttpServletRequest delegate) {
        this.delegate = delegate;
    }

    @Override
    public HttpServletRequest getContainerRequest() {
        return delegate;
    }

    @Override
    public String getRemoteHost() {
        return delegate.getRemoteHost();
    }

    @Override
    public String getRemoteAddr() {
        return this.delegate.getRemoteAddr();
    }

    @Override
    public String getMethod() {
        return delegate.getMethod();
    }


    @Override
    public String getHeader(String name) {
        return delegate.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return Pipeline.<String>of(this.delegate.getHeaders(name)).asList();
    }

    @Override
    public Collection<String> getHeaderNames() {
        return Pipeline.<String>of(delegate.getHeaderNames()).asList();
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
    public Collection<String> getAttributeNames() {
        return Pipeline.<String>of(this.delegate.getAttributeNames()).asList();
    }

    @Override
    public Locale getLocale() {
        return delegate.getLocale();
    }
    @Override
    public String getRequestURI() {
        return delegate.getRequestURI();
    }

    @Override
    public String getRequestURL() {
        return delegate.getRequestURL().toString();
    }
}

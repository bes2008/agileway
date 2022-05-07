package com.jn.agileway.ws.rs.rr;

import com.jn.agileway.http.rr.HttpRequest;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.Enumeration;
import java.util.Locale;

public class WsrsHttpRequest implements HttpRequest<ContainerRequestContext> {
    private ContainerRequestContext delegate;
    @Override
    public ContainerRequestContext getDelegate() {
        return this.delegate;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public String getMethod() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return null;
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return null;
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public void setAttribute(String name, Object value) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }
}

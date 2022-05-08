package com.jn.agileway.ws.rs.rr;

import com.jn.agileway.http.rr.HttpRequest;
import com.jn.langx.util.collection.AttributableSet;
import com.jn.langx.util.collection.iter.IterableEnumeration;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.Enumeration;
import java.util.Locale;

public class WsrsHttpRequest implements HttpRequest<ContainerRequestContext> {
    private ContainerRequestContext delegate;
    private AttributableSet attributableSet;

    public WsrsHttpRequest(ContainerRequestContext context){
        this.delegate = context;
    }
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
        return this.delegate.getMethod();
    }

    @Override
    public String getRequestURI() {
        return this.delegate.getUriInfo().getRequestUri().toString();
    }

    @Override
    public String getHeader(String name) {
        return this.delegate.getHeaderString(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return new IterableEnumeration<String>(this.delegate.getHeaders().keySet());
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return new IterableEnumeration<String>(this.delegate.getHeaders().get(name));
    }

    @Override
    public Object getAttribute(String name) {
        return this.attributableSet.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        this.attributableSet.setAttribute(name, value);
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public String getRequestURL() {
        return null;
    }
}

package com.jn.agileway.web.servlet;


import com.jn.agileway.http.rr.HttpRequest;
import com.jn.langx.util.collection.Pipeline;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Locale;

public class ServletHttpRequest implements HttpRequest<HttpServletRequest> {
    /**
     * 此时container request 代表了 Servlet 容器的请求
     */
    private HttpServletRequest containerRequest;

    public ServletHttpRequest(HttpServletRequest delegate) {
        this.containerRequest = delegate;
    }

    @Override
    public HttpServletRequest getContainerRequest() {
        return containerRequest;
    }

    @Override
    public String getRemoteHost() {
        return containerRequest.getRemoteHost();
    }

    @Override
    public String getRemoteAddr() {
        return this.containerRequest.getRemoteAddr();
    }

    @Override
    public String getMethod() {
        return containerRequest.getMethod();
    }


    @Override
    public String getHeader(String name) {
        return containerRequest.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return Pipeline.<String>of(this.containerRequest.getHeaders(name)).asList();
    }

    @Override
    public Collection<String> getHeaderNames() {
        return Pipeline.<String>of(containerRequest.getHeaderNames()).asList();
    }

    @Override
    public Object getAttribute(String name) {
        return containerRequest.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        containerRequest.setAttribute(name, value);
    }

    @Override
    public Collection<String> getAttributeNames() {
        return Pipeline.<String>of(this.containerRequest.getAttributeNames()).asList();
    }

    @Override
    public Locale getLocale() {
        return containerRequest.getLocale();
    }

    @Override
    public String getRequestURI() {
        return containerRequest.getRequestURI();
    }

    @Override
    public String getRequestURL() {
        return containerRequest.getRequestURL().toString();
    }
}

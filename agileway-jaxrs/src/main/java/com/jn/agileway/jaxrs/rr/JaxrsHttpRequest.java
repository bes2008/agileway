package com.jn.agileway.jaxrs.rr;

import com.jn.agileway.http.rr.HttpRequest;
import com.jn.langx.util.Objs;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class JaxrsHttpRequest implements HttpRequest<ContainerRequestContext> {
    /**
     * 此时的 container Request 代表了 jax-rs 容器请求
     */
    protected ContainerRequestContext containerRequest;

    public JaxrsHttpRequest(ContainerRequestContext context) {
        this.containerRequest = context;
    }

    @Override
    public ContainerRequestContext getContainerRequest() {
        return this.containerRequest;
    }


    @Override
    public String getMethod() {
        return this.containerRequest.getMethod();
    }

    @Override
    public String getRequestURI() {
        return this.containerRequest.getUriInfo().getRequestUri().toString();
    }

    @Override
    public String getHeader(String name) {
        return this.containerRequest.getHeaderString(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return this.containerRequest.getHeaders().keySet();
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return this.containerRequest.getHeaders().get(name);
    }

    @Override
    public Object getAttribute(String name) {
        return this.containerRequest.getProperty(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        this.containerRequest.setProperty(name, value);
    }

    @Override
    public Collection<String> getAttributeNames() {
        return this.containerRequest.getPropertyNames();
    }

    @Override
    public Locale getLocale() {
        List<Locale> locales = this.containerRequest.getAcceptableLanguages();
        if (Objs.isNotEmpty(locales)) {
            return locales.get(0);
        }
        return Locale.getDefault();
    }

    @Override
    public String getRequestURL() {
        return this.containerRequest.getUriInfo().getRequestUri().getPath();
    }

    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }
}

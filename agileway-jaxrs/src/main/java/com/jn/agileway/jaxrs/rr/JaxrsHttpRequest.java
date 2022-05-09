package com.jn.agileway.jaxrs.rr;

import com.jn.agileway.http.rr.HttpRequest;
import com.jn.langx.util.Objs;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class JaxrsHttpRequest implements HttpRequest<ContainerRequestContext> {
    protected ContainerRequestContext delegate;

    public JaxrsHttpRequest(ContainerRequestContext context) {
        this.delegate = context;
    }

    @Override
    public ContainerRequestContext getContainerRequest() {
        return this.delegate;
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
    public Collection<String> getHeaderNames() {
        return this.delegate.getHeaders().keySet();
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return this.delegate.getHeaders().get(name);
    }

    @Override
    public Object getAttribute(String name) {
        return this.delegate.getProperty(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        this.delegate.setProperty(name, value);
    }

    @Override
    public Collection<String> getAttributeNames() {
        return this.delegate.getPropertyNames();
    }

    @Override
    public Locale getLocale() {
        List<Locale> locales = this.delegate.getAcceptableLanguages();
        if (Objs.isNotEmpty(locales)) {
            return locales.get(0);
        }
        return Locale.getDefault();
    }

    @Override
    public String getRequestURL() {
        return this.delegate.getUriInfo().getRequestUri().getPath();
    }

    public String getRemoteAddr(){
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }
}

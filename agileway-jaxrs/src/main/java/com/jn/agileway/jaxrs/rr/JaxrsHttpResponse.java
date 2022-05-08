package com.jn.agileway.jaxrs.rr;

import com.jn.agileway.http.rr.HttpResponse;

import javax.ws.rs.container.ContainerResponseContext;
import java.util.Collection;

public class JaxrsHttpResponse implements HttpResponse<ContainerResponseContext> {
    private ContainerResponseContext delegate;

    public JaxrsHttpResponse(ContainerResponseContext context) {
        this.delegate = context;
    }

    public ContainerResponseContext getDelegate() {
        return this.delegate;
    }

    @Override
    public void addHeader(String name, String value) {
        // unsupported
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
        return this.delegate.getStringHeaders().get(name);
    }

    @Override
    public int getStatusCode() {
        return this.delegate.getStatus();
    }
}

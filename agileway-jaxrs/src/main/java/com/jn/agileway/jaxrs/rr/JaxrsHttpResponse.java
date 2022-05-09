package com.jn.agileway.jaxrs.rr;

import com.jn.agileway.http.rr.HttpResponse;

import javax.ws.rs.container.ContainerResponseContext;
import java.util.Collection;

public class JaxrsHttpResponse implements HttpResponse<ContainerResponseContext> {
    private ContainerResponseContext containerResponse;

    public JaxrsHttpResponse(ContainerResponseContext context) {
        this.containerResponse = context;
    }

    public ContainerResponseContext getContainerResponse() {
        return this.containerResponse;
    }

    @Override
    public void addHeader(String name, String value) {
        // unsupported
    }

    @Override
    public String getHeader(String name) {
        return this.containerResponse.getHeaderString(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return this.containerResponse.getHeaders().keySet();
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return this.containerResponse.getStringHeaders().get(name);
    }

    @Override
    public int getStatusCode() {
        return this.containerResponse.getStatus();
    }
}

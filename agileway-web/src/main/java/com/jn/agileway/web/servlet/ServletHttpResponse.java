package com.jn.agileway.web.servlet;


import com.jn.agileway.http.rr.HttpResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public class ServletHttpResponse implements HttpResponse<HttpServletResponse> {
    private HttpServletResponse containerResponse;

    public ServletHttpResponse(HttpServletResponse response){
        this.containerResponse = response;
    }
    @Override
    public HttpServletResponse getContainerResponse() {
        return containerResponse;
    }

    @Override
    public void addHeader(String name, String value) {
        containerResponse.addHeader(name,value);
    }

    @Override
    public String getHeader(String name) {
        return containerResponse.getHeader(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return containerResponse.getHeaderNames();
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return containerResponse.getHeaders(name);
    }

    @Override
    public int getStatusCode() {
        return containerResponse.getStatus();
    }
}

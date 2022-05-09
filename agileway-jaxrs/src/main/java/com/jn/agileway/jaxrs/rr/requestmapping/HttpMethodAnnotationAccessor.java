package com.jn.agileway.jaxrs.rr.requestmapping;

import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessor;

import javax.ws.rs.HttpMethod;
import java.util.List;

public class HttpMethodAnnotationAccessor implements RequestMappingAccessor<HttpMethod> {
    private HttpMethod httpMethod;
    @Override
    public HttpMethod getMapping() {
        return httpMethod;
    }

    @Override
    public void setMapping(HttpMethod mapping) {
        this.httpMethod= mapping;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public List<String> values() {
        return null;
    }

    @Override
    public List<String> paths() {
        return null;
    }

    @Override
    public List<com.jn.langx.util.net.http.HttpMethod> methods() {
        return null;
    }

    @Override
    public List<String> params() {
        return null;
    }

    @Override
    public List<String> headers() {
        return null;
    }

    @Override
    public List<String> consumes() {
        return null;
    }

    @Override
    public List<String> produces() {
        return null;
    }
}

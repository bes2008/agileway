package com.jn.agileway.spring.web.mvc.requestmapping;

import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessor;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.net.http.HttpMethod;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

public class PutMappingAnnotationAccessor implements RequestMappingAccessor<PutMapping> {
    private PutMapping mapping;

    @Override
    public PutMapping getMapping() {
        return mapping;
    }

    @Override
    public void setMapping(PutMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public String name() {
        return mapping.name();
    }

    @Override
    public List<String> values() {
        return Collects.newArrayList(mapping.value());
    }

    @Override
    public List<String> paths() {
        return Collects.newArrayList(mapping.path());
    }

    @Override
    public List<HttpMethod> methods() {
        return Collects.newArrayList(HttpMethod.PUT);
    }

    @Override
    public List<String> params() {
        return Collects.newArrayList(mapping.params());
    }

    @Override
    public List<String> headers() {
        return Collects.newArrayList(mapping.headers());
    }

    @Override
    public List<String> consumes() {
        return Collects.newArrayList(mapping.consumes());
    }

    @Override
    public List<String> produces() {
        return Collects.newArrayList(mapping.produces());
    }
}

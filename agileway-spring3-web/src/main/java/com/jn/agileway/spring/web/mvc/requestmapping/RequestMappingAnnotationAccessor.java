package com.jn.agileway.spring.web.mvc.requestmapping;

import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessor;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.enums.Enums;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.net.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public class RequestMappingAnnotationAccessor implements RequestMappingAccessor<RequestMapping> {

    private RequestMapping mapping;

    @Override
    public void setMapping(RequestMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public RequestMapping getMapping() {
        return mapping;
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
        return Pipeline.of(mapping.method()).map(new Function<RequestMethod, HttpMethod>() {
            @Override
            public HttpMethod apply(RequestMethod method) {
                return Enums.ofName(HttpMethod.class,method.name());
            }
        }).asList();
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

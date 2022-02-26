package com.jn.agileway.spring.web.mvc.requestmapping;

import com.jn.langx.util.collection.Collects;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public class PostMappingAnnotationAccessor implements RequestMappingAccessor<PostMapping> {
    private PostMapping mapping;

    @Override
    public PostMapping getMapping() {
        return mapping;
    }

    @Override
    public void setMapping(PostMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public String getName() {
        return mapping.name();
    }

    @Override
    public List<String> getValues() {
        return Collects.newArrayList(mapping.value());
    }

    @Override
    public List<String> getPaths() {
        return Collects.newArrayList(mapping.path());
    }

    @Override
    public List<RequestMethod> getMethods() {
        return Collects.newArrayList(RequestMethod.POST);
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

package com.jn.agileway.http.rr.requestmapping;

import com.jn.langx.util.net.http.HttpMethod;

import java.lang.annotation.Annotation;
import java.util.List;

public interface RequestMappingAccessor<E extends Annotation> {
    E getMapping();

    void setMapping(E mapping);

    String name();

    List<String> values();

    List<String> paths();

    List<HttpMethod> methods();

    List<String> params();

    List<String> headers();

    List<String> consumes();

    List<String> produces();
}

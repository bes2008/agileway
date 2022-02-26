package com.jn.agileway.spring.web.mvc.requestmapping;

import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.util.List;

public interface RequestMappingAccessor<E extends Annotation> {
    E getMapping();

    void setMapping(E mapping);

    String name();

    List<String> values();

    List<String> paths();

    List<RequestMethod> methods();

    List<String> params();

    List<String> headers();

    List<String> consumes();

    List<String> produces();
}

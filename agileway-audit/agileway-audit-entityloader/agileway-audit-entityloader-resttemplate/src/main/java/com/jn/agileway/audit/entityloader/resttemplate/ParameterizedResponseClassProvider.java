package com.jn.agileway.audit.entityloader.resttemplate;

import com.jn.agileway.audit.core.model.ResourceDefinition;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

public interface ParameterizedResponseClassProvider {
    <T> ParameterizedTypeReference<T> get(String url, HttpMethod httpMethod, ResourceDefinition resourceDefinition);
}

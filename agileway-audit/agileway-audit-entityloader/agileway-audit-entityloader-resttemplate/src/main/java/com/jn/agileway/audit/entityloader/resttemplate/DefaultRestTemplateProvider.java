package com.jn.agileway.audit.entityloader.resttemplate;

import com.jn.agileway.audit.core.model.ResourceDefinition;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class DefaultRestTemplateProvider implements RestTemplateProvider {
    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public RestTemplate get(String url, HttpMethod httpMethod, ResourceDefinition resourceDefinition) {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}

package com.jn.agileway.audit.entityloader.resttemplate;

import com.jn.agileway.audit.core.model.ResourceDefinition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.io.Serializable;
import java.util.List;

public interface HttpRequestProvider {
    /**
     * 如果是批量模式，这个方法只会调用一次，所以index 为 0，并且要将 ids全放进去
     *
     * @param url                url
     * @param method             http method
     * @param resourceDefinition the resource definition
     * @param stepEntityIds      all entity ids in a step
     * @return
     */
    HttpEntity get(String url, HttpMethod method, ResourceDefinition resourceDefinition, List<Serializable> stepEntityIds);
}

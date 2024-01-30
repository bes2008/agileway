package com.jn.agileway.audit.entityloader.resttemplate;

import com.jn.langx.http.rest.RestRespBody;
import org.springframework.http.ResponseEntity;

public class DefaultResourceEntityExtractor implements ResourceEntityExtractor {
    @Override
    public Object extract(ResponseEntity response) {
        if (response.hasBody()) {
            Object obj = response.getBody();
            if (obj instanceof RestRespBody) {
                RestRespBody restRespBody = (RestRespBody) obj;
                return restRespBody.getData();
            }
            return obj;
        }
        return null;
    }
}

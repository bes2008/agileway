package com.jn.agileway.httpclient.declarative;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.langx.Factory;
import com.jn.langx.util.collection.multivalue.LinkedMultiValueMap;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.valuegetter.ArrayValueGetter;

import java.util.Collection;
import java.util.Map;

public class HttpRequestFactory implements Factory<Object[], HttpRequest<?>> {
    private HttpExchangeMethod httpExchangeMethod;

    public HttpRequestFactory(HttpExchangeMethod httpExchangeMethod) {
        this.httpExchangeMethod = httpExchangeMethod;
    }

    public HttpRequest<?> get(Object[] methodArgs) {
        MultiValueMap<String, Object> queryParams = resolveQueryParams(methodArgs);
        Map<String, Object> uriVariables = resolveUriVariables(methodArgs);
        HttpHeaders headers = resolveHeaders(methodArgs);
        Object body = resolveBody(methodArgs);
        return HttpRequest.create(
                httpExchangeMethod.getHttpMethod(),
                httpExchangeMethod.getUriTemplate(),
                queryParams,
                uriVariables,
                headers,
                body);
    }

    private MultiValueMap<String, Object> resolveQueryParams(Object[] methodArgs) {
        MultiValueMap<String, Object> queryParams = null;
        Map<String, ArrayValueGetter<Object>> queryParamsDefinitionMap = httpExchangeMethod.getQueryParams();
        if (queryParamsDefinitionMap == null) {
            return queryParams;
        }
        queryParams = new LinkedMultiValueMap<>();
        for (Map.Entry<String, ArrayValueGetter<Object>> entry : queryParamsDefinitionMap.entrySet()) {
            String queryParamName = entry.getKey();
            ArrayValueGetter<Object> valuesGetter = entry.getValue();
            Object values = valuesGetter.get(methodArgs);
            if (values == null) {
                queryParams.add(queryParamName, "");
            } else if (values instanceof Object[]) {
                queryParams.addAll(queryParamName, (Object[]) values);
            } else if (values instanceof Collection) {
                queryParams.addAll(queryParamName, (Collection) values);
            }
        }
        return queryParams;
    }

    private Map<String, Object> resolveUriVariables(Object[] methodArgs) {
        Map<String, Object> uriVariables = null;
        return uriVariables;
    }


    private Object resolveBody(Object[] methodArgs) {
        Object body = null;
        return body;
    }

    private HttpHeaders resolveHeaders(Object[] methodArgs) {
        HttpHeaders headers = null;
        return headers;
    }

}

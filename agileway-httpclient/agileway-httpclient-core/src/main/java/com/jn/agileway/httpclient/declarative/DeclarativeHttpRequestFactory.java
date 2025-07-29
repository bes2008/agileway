package com.jn.agileway.httpclient.declarative;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.langx.Factory;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.multivalue.LinkedMultiValueMap;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.valuegetter.ArrayValueGetter;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DeclarativeHttpRequestFactory implements Factory<Object[], HttpRequest<?>> {
    private String baseUri;
    private Charset uriEncoding;
    private HttpExchangeMethod httpExchangeMethod;

    public DeclarativeHttpRequestFactory(String baseUri, Charset uriEncoding, HttpExchangeMethod httpExchangeMethod) {
        this.baseUri = baseUri;
        this.httpExchangeMethod = httpExchangeMethod;
    }

    public HttpRequest<?> get(Object[] methodArgs) {
        MultiValueMap<String, Object> queryParams = buildQueryParams(methodArgs);
        Map<String, Object> uriVariables = buildUriVariables(methodArgs);
        HttpHeaders headers = buildHeaders(methodArgs);
        Object body = buildBody(methodArgs);
        Charset uriEncoding = this.uriEncoding;
        String uri = Strings.stripEnd(baseUri, "/") + "/" + Strings.stripStart(httpExchangeMethod.getUriTemplate(), "/");
        return HttpRequest.create(
                httpExchangeMethod.getHttpMethod(),
                uri,
                uriEncoding,
                queryParams,
                uriVariables,
                headers,
                body);
    }

    private MultiValueMap<String, Object> buildQueryParams(Object[] methodArgs) {
        MultiValueMap<String, Object> queryParams = null;
        Map<String, DefaultValueSupportedValueGetter> queryParamsDefinitionMap = httpExchangeMethod.getQueryParams();
        if (Objs.isEmpty(queryParamsDefinitionMap)) {
            return queryParams;
        }
        queryParams = new LinkedMultiValueMap<>();
        for (Map.Entry<String, DefaultValueSupportedValueGetter> entry : queryParamsDefinitionMap.entrySet()) {
            String queryParamName = entry.getKey();
            ArrayValueGetter<Object> valuesGetter = entry.getValue();
            Object values = valuesGetter.get(methodArgs);
            if (values == null) {
                queryParams.add(queryParamName, "");
            } else if (values instanceof Object[]) {
                queryParams.addAll(queryParamName, (Object[]) values);
            } else if (values instanceof Collection) {
                queryParams.addAll(queryParamName, (Collection) values);
            } else {
                queryParams.add(queryParamName, values);
            }
        }
        return queryParams;
    }

    private Map<String, Object> buildUriVariables(Object[] methodArgs) {
        Map<String, Object> uriVariables = null;

        Map<String, ArrayValueGetter<Object>> uriVariablesDefinitionMap = httpExchangeMethod.getUriVariables();
        if (Objs.isEmpty(uriVariablesDefinitionMap)) {
            return uriVariables;
        }
        uriVariables = new HashMap<String, Object>();
        for (Map.Entry<String, ArrayValueGetter<Object>> entry : uriVariablesDefinitionMap.entrySet()) {
            String uriVariableName = entry.getKey();
            ArrayValueGetter<Object> valuesGetter = entry.getValue();
            Object values = valuesGetter.get(methodArgs);
            if (values == null) {
                uriVariables.put(uriVariableName, "");
            } else if (values instanceof Object[]) {
                uriVariables.put(uriVariableName, Strings.join(",", (Object[]) values));
            } else if (values instanceof Collection) {
                uriVariables.put(uriVariableName, Strings.join(",", (Collection) values));
            } else {
                uriVariables.put(uriVariableName, values);
            }
        }

        return uriVariables;
    }


    private Object buildBody(Object[] methodArgs) {
        Object body = null;

        ArrayValueGetter<Object> bodyGetter = httpExchangeMethod.getBody();
        if (bodyGetter != null) {
            body = bodyGetter.get(methodArgs);
            return body;
        }

        Map<String, ArrayValueGetter<Object>> bodyPartsDefinitionMap = httpExchangeMethod.getBodyParts();
        if (Objs.isEmpty(bodyPartsDefinitionMap)) {
            return body;
        }
        MultiValueMap<String, Object> bodyPartMap = new LinkedMultiValueMap<>();
        for (Map.Entry<String, ArrayValueGetter<Object>> entry : bodyPartsDefinitionMap.entrySet()) {
            String bodyPartName = entry.getKey();
            ArrayValueGetter<Object> valuesGetter = entry.getValue();
            Object values = valuesGetter.get(methodArgs);
            if (values == null) {
                bodyPartMap.add(bodyPartName, "");
            } else if (values instanceof Object[]) {
                bodyPartMap.addAll(bodyPartName, (Object[]) values);
            } else if (values instanceof Collection) {
                bodyPartMap.addAll(bodyPartName, (Collection) values);
            } else {
                bodyPartMap.add(bodyPartName, values);
            }
        }
        return bodyPartMap;
    }

    private HttpHeaders buildHeaders(Object[] methodArgs) {
        HttpHeaders headers = new HttpHeaders();

        String[] accept = httpExchangeMethod.getAccept();
        if (Objs.isNotEmpty(accept)) {
            headers.setAccept(Pipeline.of(accept).map(new Function<String, MediaType>() {
                @Override
                public MediaType apply(String v) {
                    return MediaType.valueOf(v);
                }
            }).asList());
        }

        MediaType contentType = httpExchangeMethod.getContentType();
        if (contentType != null) {
            headers.setContentType(contentType);
        }

        Map<String, ArrayValueGetter<Object>> headersDefinitionMap = httpExchangeMethod.getHeaders();
        if (Objs.isEmpty(headersDefinitionMap)) {
            return headers;
        }
        for (Map.Entry<String, ArrayValueGetter<Object>> entry : headersDefinitionMap.entrySet()) {
            String headerName = entry.getKey();
            ArrayValueGetter<Object> valuesGetter = entry.getValue();
            Object values = valuesGetter.get(methodArgs);
            if (values == null) {
                headers.add(headerName, "");
            } else if (values instanceof Object[]) {
                headers.addAll(headerName, Pipeline.of((Object[]) values).map(new Function<Object, String>() {
                    @Override
                    public String apply(Object v) {
                        return Objs.toStringOrNull(v);
                    }
                }).clearEmptys().asList());
            } else if (values instanceof Collection) {
                headers.addAll(headerName, Pipeline.of((Collection<Object>) values).map(new Function<Object, String>() {
                    @Override
                    public String apply(Object v) {
                        return Objs.toStringOrNull(v);
                    }
                }).clearEmptys().asList());
            } else {
                headers.add(headerName, Objs.toStringOrNull(values));
            }
        }
        return headers;
    }

}

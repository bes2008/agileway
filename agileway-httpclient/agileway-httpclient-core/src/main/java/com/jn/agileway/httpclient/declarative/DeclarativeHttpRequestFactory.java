package com.jn.agileway.httpclient.declarative;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.langx.Factory;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Arrs;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.multivalue.LinkedMultiValueMap;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.function.Consumer;
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
        this.uriEncoding = uriEncoding;
        this.httpExchangeMethod = httpExchangeMethod;
    }

    public HttpRequest<?> get(Object[] methodArgs) {
        MultiValueMap<String, Object> queryParams = buildQueryParams(methodArgs);
        Map<String, Object> uriVariables = buildUriVariables(methodArgs);
        HttpHeaders headers = buildHeaders(methodArgs);
        Object body = buildBody(methodArgs);
        Charset uriEncoding = httpExchangeMethod.getUriEncoding();
        if (uriEncoding == null) {
            uriEncoding = this.uriEncoding;
        }
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

        Map<String, DefaultValueSupportedValueGetter> queryParamsDefinitionMap = httpExchangeMethod.getQueryParams();
        if (Objs.isEmpty(queryParamsDefinitionMap)) {
            return null;
        }
        final MultiValueMap<String, Object> queryParams = new LinkedMultiValueMap<>();
        for (Map.Entry<String, DefaultValueSupportedValueGetter> entry : queryParamsDefinitionMap.entrySet()) {
            String queryParamName = entry.getKey();
            ArrayValueGetter<Object> valuesGetter = entry.getValue();
            Object values = valuesGetter.get(methodArgs);
            if (values == null) {
                queryParams.add(queryParamName, "");
            } else if (values instanceof Collection) {
                queryParams.addAll(queryParamName, (Collection) values);
            } else if (Arrs.isArray(values)) {
                Pipeline.of(values).forEach(new Consumer<Object>() {
                    @Override
                    public void accept(Object v) {
                        queryParams.add(queryParamName, v);
                    }
                });
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
            } else if (values instanceof Collection || Arrs.isArray(values)) {
                uriVariables.put(uriVariableName, Strings.join(",", values));
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
            } else if (values instanceof MultiValueMap) {
                MultiValueMap<String, ?> valuesMap = (MultiValueMap<String, ?>) values;
                for (String key : valuesMap.keySet()) {
                    bodyPartMap.addAll(key, valuesMap.get(key));
                }
            } else if (values instanceof Map) {
                Map<String, ?> map = (Map<String, ?>) values;
                for (String key : map.keySet()) {
                    bodyPartMap.add(key, map.get(key));
                }
            } else if (values instanceof Collection) {
                bodyPartMap.addAll(bodyPartName, (Collection) values);
            } else if (Arrs.isArray(values)) {
                Pipeline.of(values).forEach(new Consumer<Object>() {
                    @Override
                    public void accept(Object v) {
                        bodyPartMap.add(bodyPartName, v);
                    }
                });
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
            } else if (values instanceof MultiValueMap) {
                MultiValueMap<String, ?> multiValueMap = (MultiValueMap<String, ?>) values;
                for (String key : multiValueMap.keySet()) {
                    Collection<?> values1 = multiValueMap.get(key);
                    headers.addAll(key, Pipeline.<Object>of(values1).map(toStringFunction).asList());
                }
            } else if (values instanceof Map) {
                Map<String, ?> map = (Map<String, ?>) values;
                for (Map.Entry<String, ?> entry1 : map.entrySet()) {
                    headers.add(entry1.getKey(), entry1.getValue().toString());
                }
            } else if (values instanceof Collection || Arrs.isArray(values)) {
                headers.addAll(headerName, Pipeline.of(values).map(toStringFunction).clearEmptys().asList());
            } else {
                headers.add(headerName, Objs.toStringOrNull(values));
            }
        }
        return headers;
    }

    private static final Function<Object, String> toStringFunction = new Function<Object, String>() {
        @Override
        public String apply(Object v) {
            return Objs.toStringOrNull(v);
        }
    };
}

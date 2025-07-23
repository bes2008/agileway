package com.jn.agileway.httpclient.declarative;

import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.valuegetter.ArrayValueGetter;

import java.util.Map;

public class HttpExchangeMethod {
    private String uriTemplate;

    private HttpMethod method;

    private Map<String, ArrayValueGetter<Object>> queryParams;

    private Map<String, ArrayValueGetter<Object>> uriVariables;

    private Map<String, ArrayValueGetter<Object>> headers;

    private Map<String, ArrayValueGetter<Object>> cookies;

    private Map<String, ArrayValueGetter<Object>> bodyParts;
}

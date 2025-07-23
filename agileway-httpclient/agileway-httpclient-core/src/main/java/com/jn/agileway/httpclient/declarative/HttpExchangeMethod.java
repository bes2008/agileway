package com.jn.agileway.httpclient.declarative;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.reflect.signature.TypeSignatures;
import com.jn.langx.util.valuegetter.ArrayValueGetter;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpExchangeMethod {
    private String uriTemplate;

    private HttpMethod httpMethod;

    private Method javaMethod;

    private MediaType contentType;

    private String[] accept;

    private Map<String, QueryParamValueGetter> queryParams = new LinkedHashMap<>();

    private Map<String, ArrayValueGetter<Object>> uriVariables = new LinkedHashMap<>();

    private Map<String, ArrayValueGetter<Object>> headers;

    private Map<String, ArrayValueGetter<Object>> cookies;

    private Map<String, ArrayValueGetter<Object>> bodyParts;

    private ArrayValueGetter<Object> body;

    public String getUriTemplate() {
        return uriTemplate;
    }

    public void setUriTemplate(String uriTemplate) {
        this.uriTemplate = uriTemplate;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Method getJavaMethod() {
        return javaMethod;
    }

    public void setJavaMethod(Method javaMethod) {
        this.javaMethod = javaMethod;
    }

    public Map<String, QueryParamValueGetter> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, QueryParamValueGetter> queryParams) {
        this.queryParams = queryParams;
    }

    public Map<String, ArrayValueGetter<Object>> getUriVariables() {
        return uriVariables;
    }

    public void setUriVariables(Map<String, ArrayValueGetter<Object>> uriVariables) {
        this.uriVariables = uriVariables;
    }

    public Map<String, ArrayValueGetter<Object>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, ArrayValueGetter<Object>> headers) {
        this.headers = headers;
    }

    public Map<String, ArrayValueGetter<Object>> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, ArrayValueGetter<Object>> cookies) {
        this.cookies = cookies;
    }

    public Map<String, ArrayValueGetter<Object>> getBodyParts() {
        return bodyParts;
    }

    public void setBodyParts(Map<String, ArrayValueGetter<Object>> bodyParts) {
        this.bodyParts = bodyParts;
    }

    public ArrayValueGetter<Object> getBody() {
        return body;
    }

    public MediaType getContentType() {
        return contentType;
    }

    public void setContentType(MediaType contentType) {
        this.contentType = contentType;
    }

    public String[] getAccept() {
        return accept;
    }

    public void setAccept(String[] accept) {
        this.accept = accept;
    }

    public void setBody(ArrayValueGetter<Object> body) {
        this.body = body;
    }

    public void checkValid() throws HttpExchangeMethodDeclaringException {
        if (javaMethod == null) {
            throw new HttpExchangeMethodDeclaringException("The java method is null");
        }
        if (httpMethod == null || httpMethod == HttpMethod.OPTIONS || httpMethod == HttpMethod.TRACE || httpMethod == HttpMethod.HEAD) {
            throw new HttpExchangeMethodDeclaringException(StringTemplates.formatWithPlaceholder("The http method is not valid, http exchange java method: {}", TypeSignatures.toMethodSignature(javaMethod)));
        }
        if (Strings.isBlank(uriTemplate)) {
            throw new HttpExchangeMethodDeclaringException(StringTemplates.formatWithPlaceholder("The uriTemplate is blank, http exchange java method: {}", TypeSignatures.toMethodSignature(javaMethod)));
        }
        if (body != null && bodyParts != null) {
            throw new HttpExchangeMethodDeclaringException(StringTemplates.formatWithPlaceholder("The body and bodyParts are both set, http exchange java method: {}", TypeSignatures.toMethodSignature(javaMethod)));
        }
    }
}

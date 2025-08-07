package com.jn.agileway.httpclient.declarative;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.reflect.signature.TypeSignatures;
import com.jn.langx.util.valuegetter.ArrayValueGetter;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpExchangeMethod {
    private String uriTemplate;

    private HttpMethod httpMethod;

    private Method javaMethod;

    private MediaType contentType;

    private String[] accept;

    @Nullable
    private Charset uriEncoding;

    private final Map<String, DefaultValueSupportedValueGetter> queryParams = new LinkedHashMap<>();

    private final Map<String, ArrayValueGetter<Object>> uriVariables = new LinkedHashMap<>();

    private final Map<String, ArrayValueGetter<Object>> headers = new LinkedHashMap<>();

    private final Map<String, ArrayValueGetter<Object>> cookies = new LinkedHashMap<>();

    private final Map<String, ArrayValueGetter<Object>> bodyParts = new LinkedHashMap<>();

    private ArrayValueGetter<Object> body;

    /**
     * http response 的 content 对应的 Java类型
     */
    private Type expectedResponseType;

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

    public Map<String, DefaultValueSupportedValueGetter> getQueryParams() {
        return queryParams;
    }


    public Map<String, ArrayValueGetter<Object>> getUriVariables() {
        return uriVariables;
    }

    public Map<String, ArrayValueGetter<Object>> getHeaders() {
        return headers;
    }


    public Map<String, ArrayValueGetter<Object>> getCookies() {
        return cookies;
    }


    public Map<String, ArrayValueGetter<Object>> getBodyParts() {
        return bodyParts;
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

    public Type getExpectedResponseType() {
        return expectedResponseType;
    }

    public void setExpectedResponseType(Type expectedResponseType) {
        this.expectedResponseType = expectedResponseType;
    }

    public void checkValid() throws HttpExchangeMethodDeclaringException {
        // java method
        if (javaMethod == null) {
            throw new HttpExchangeMethodDeclaringException("The java method is required");
        }
        // http method
        if (httpMethod == null || httpMethod == HttpMethod.OPTIONS || httpMethod == HttpMethod.TRACE || httpMethod == HttpMethod.HEAD) {
            throw new HttpExchangeMethodDeclaringException(StringTemplates.formatWithPlaceholder("The http method is not valid, http exchange java method: {}", TypeSignatures.toMethodSignature(javaMethod)));
        }
        // SOAP 方法时，uri template 为 ""
        if (uriTemplate == null) {
            throw new HttpExchangeMethodDeclaringException(StringTemplates.formatWithPlaceholder("The uriTemplate is blank, http exchange java method: {}", TypeSignatures.toMethodSignature(javaMethod)));
        }
        // body 和 bodyParts 不能同时设置
        if (body != null && !bodyParts.isEmpty()) {
            throw new HttpExchangeMethodDeclaringException(StringTemplates.formatWithPlaceholder("The body and bodyParts are both set, http exchange java method: {}", TypeSignatures.toMethodSignature(javaMethod)));
        }
        // 对 expectedResponseType 进行校验
        if (expectedResponseType != null) {
            if (expectedResponseType == Promise.class || expectedResponseType == HttpResponse.class) {
                throw new HttpExchangeMethodDeclaringException(StringTemplates.formatWithPlaceholder("The expectedResponseType is not valid, http exchange java method: {}", TypeSignatures.toMethodSignature(javaMethod)));
            }
            if (expectedResponseType instanceof ParameterizedType) {
                ParameterizedType prt = (ParameterizedType) expectedResponseType;
                Type rawType = prt.getRawType();
                if (rawType == Promise.class || rawType == HttpResponse.class) {
                    throw new HttpExchangeMethodDeclaringException(StringTemplates.formatWithPlaceholder("The expectedResponseType is not valid, http exchange java method: {}", TypeSignatures.toMethodSignature(javaMethod)));
                }
            }
        }
    }

    public Charset getUriEncoding() {
        return uriEncoding;
    }

    public void setUriEncoding(Charset uriEncoding) {
        this.uriEncoding = uriEncoding;
    }
}

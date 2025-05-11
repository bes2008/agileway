package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpRequestInterceptor;
import com.jn.agileway.httpclient.core.exception.HttpRequestInvalidException;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.net.http.HttpMethod;

import java.util.List;

public class HttpRequestMethodInterceptor implements HttpRequestInterceptor {
    private List<HttpMethod> allowedMethods;
    private List<HttpMethod> notAllowedMethods;

    public HttpRequestMethodInterceptor(List<String> allowedMethods, List<String> notAllowedMethods) {
        setAllowedMethods(allowedMethods);
        setNotAllowedMethods(notAllowedMethods);
    }

    @Override
    public void intercept(HttpRequest request) {
        if (HttpClientUtils.isForm(request.getHeaders())) {
            if (request.getMethod() != HttpMethod.POST) {
                throw new HttpRequestInvalidException(StringTemplates.formatWithPlaceholder("Http request with Content-Type {}, method {} is invalid", request.getHeaders().getContentType(), request.getMethod()));
            }
        }
        if (!isAllowedMethod(request.getMethod())) {
            throw new HttpRequestInvalidException(StringTemplates.formatWithPlaceholder("Http request method {} is not allowed", request.getMethod()));
        }
    }

    public void setAllowedMethods(List<String> theAllowedMethods) {
        this.allowedMethods = Pipeline.<String>of(theAllowedMethods)
                .map(new Function<String, HttpMethod>() {
                    @Override
                    public HttpMethod apply(String method) {
                        return HttpMethod.resolve(method);
                    }
                }).asList();
    }

    public void setNotAllowedMethods(List<String> theNotAllowedMethods) {
        this.notAllowedMethods = Pipeline.<String>of(theNotAllowedMethods)
                .map(new Function<String, HttpMethod>() {
                    @Override
                    public HttpMethod apply(String method) {
                        return HttpMethod.resolve(method);
                    }
                }).asList();
    }

    private boolean isAllowedMethod(HttpMethod method) {
        boolean allowed = false;
        if (Objs.isEmpty(allowedMethods)) {
            allowed = true;
        } else {
            if (allowedMethods.contains(method)) {
                allowed = true;
            }
        }
        if (!allowed) {
            return false;
        }
        if (Objs.isEmpty(notAllowedMethods)) {
            return true;
        }
        if (notAllowedMethods.contains(method)) {
            allowed = false;
        }
        return allowed;

    }
}

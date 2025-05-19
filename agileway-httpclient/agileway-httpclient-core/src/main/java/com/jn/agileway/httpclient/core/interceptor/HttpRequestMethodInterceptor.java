package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpRequestInterceptor;
import com.jn.agileway.httpclient.core.exception.BadHttpRequestException;
import com.jn.agileway.httpclient.core.exception.MethodNotAllowedRequestException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.exclusion.IncludeExcludePredicate;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.net.http.HttpMethod;

import java.util.List;

/**
 * 对 method 进行校验
 */
public class HttpRequestMethodInterceptor implements HttpRequestInterceptor {
    private List<HttpMethod> allowedMethods;
    private List<HttpMethod> notAllowedMethods;

    public HttpRequestMethodInterceptor(List<String> allowedMethods, List<String> notAllowedMethods) {
        setAllowedMethods(allowedMethods);
        setNotAllowedMethods(notAllowedMethods);
    }

    @Override
    public void intercept(HttpRequest request) {
        if (request.getMethod() == null) {
            throw new BadHttpRequestException("HTTP method is required");
        }
        if (!isAllowedMethod(request.getMethod())) {
            throw new MethodNotAllowedRequestException(StringTemplates.formatWithPlaceholder("Http request method {} is not allowed", request.getMethod()));
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
        if (Objs.isEmpty(theNotAllowedMethods)) {
            theNotAllowedMethods = Lists.asList("head", "options", "trace");
        }
        this.notAllowedMethods = Pipeline.<String>of(theNotAllowedMethods)
                .map(new Function<String, HttpMethod>() {
                    @Override
                    public HttpMethod apply(String method) {
                        return HttpMethod.resolve(method);
                    }
                }).asList();
    }

    private boolean isAllowedMethod(HttpMethod method) {
        return new IncludeExcludePredicate<HttpMethod, HttpMethod>(this.allowedMethods, this.notAllowedMethods).test(method);
    }
}

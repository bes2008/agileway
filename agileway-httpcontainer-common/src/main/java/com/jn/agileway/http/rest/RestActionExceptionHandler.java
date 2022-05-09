package com.jn.agileway.http.rest;

import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.langx.http.rest.RestRespBody;

public interface RestActionExceptionHandler<T> {
    RestRespBody<T> handle(HttpRequest request, HttpResponse response, Object handler, Exception ex);
}

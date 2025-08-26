package com.jn.agileway.httpclient;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.langx.util.concurrent.promise.Promise;

import java.lang.reflect.Type;

public interface Exchanger {
    <T> Promise<HttpResponse<T>> exchange(boolean async, HttpRequest<?> request, Type responseType);
}

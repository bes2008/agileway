package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpResponse;

public interface HttpResponseInterceptor {
    void intercept(HttpResponse response);
}

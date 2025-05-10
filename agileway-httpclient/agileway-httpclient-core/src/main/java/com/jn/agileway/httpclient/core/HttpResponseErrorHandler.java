package com.jn.agileway.httpclient.core;

import com.jn.langx.util.function.Handler;

public interface HttpResponseErrorHandler extends Handler<HttpResponse<?>> {
    boolean isError(HttpResponse<?> response);

    @Override
    void handle(HttpResponse<?> httpResponse);
}

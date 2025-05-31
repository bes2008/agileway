package com.jn.agileway.httpclient.protocol.restful;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.agileway.httpclient.core.HttpExchanger;

import java.lang.reflect.Type;

public class RestfulExchanger {
    private HttpExchanger httpExchanger;

    public RestfulExchanger(HttpExchanger httpExchanger) {
        this.httpExchanger = httpExchanger;
    }

    public <O> HttpResponse<O> exchange(HttpRequest request, Type responseType) {
        return this.<O>exchangeAsync(request, responseType).await();
    }

    public <O> Promise<HttpResponse<O>> exchangeAsync(HttpRequest request, Type responseType) {
        request.getHttpHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
        Promise promise = httpExchanger.exchange(true, request, responseType);
        return promise;
    }

}

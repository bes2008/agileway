package com.jn.agileway.httpclient.core;

import com.jn.langx.exception.ErrorHandler;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.concurrent.promise.Task;
import com.jn.langx.util.function.Handler;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.Executor;

public class HttpExchanger {

    private Executor executor;
    private HttpRequestFactory requestFactory;

    public Promise<HttpResponse> exchange(URI uri, HttpMethod method, HttpHeaders headers, OutputStream body) {
        return new Promise<HttpResponse>(executor, new Task<HttpResponse>() {
            @Override
            public HttpResponse run(Handler<HttpResponse> resolve, ErrorHandler reject) {
                try {
                    HttpRequest request = requestFactory.create(method, uri);
                    request.setHeaders(headers);
                    request.setBody(body);
                    return request.exchange();
                } catch (Throwable ex) {
                    reject.handle(ex);
                    return null;
                }
            }
        });
    }
}

package com.jn.agileway.httpclient.core;

import com.jn.langx.exception.ErrorHandler;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.concurrent.promise.Task;
import com.jn.langx.util.function.Handler;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.retry.RetryConfig;
import com.jn.langx.util.retry.Retryer;

import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class HttpExchanger {

    private Executor executor;
    private HttpRequestFactory requestFactory;


    public Promise<HttpResponse> exchange(URI uri, HttpMethod method, HttpHeaders headers, OutputStream body, final RetryConfig retryConfig) {
        return new Promise<HttpResponse>(executor, new Task<HttpResponse>() {

            @Override
            public HttpResponse run(Handler<HttpResponse> resolve, ErrorHandler reject) {
                RetryConfig theRetryConfig = retryConfig;
                if (theRetryConfig == null) {
                    theRetryConfig = RetryConfig.noneRetryConfig();
                }
                try {
                    return Retryer.<HttpResponse>execute(new Predicate<Throwable>() {
                        @Override
                        public boolean test(Throwable ex) {
                            return false;
                        }
                    }, new Predicate<HttpResponse>() {
                        @Override
                        public boolean test(HttpResponse value) {
                            return false;
                        }
                    }, theRetryConfig, null, new Callable<HttpResponse>() {
                        @Override
                        public HttpResponse call() throws Exception {
                            try {
                                HttpRequest request = requestFactory.create(method, uri);
                                request.setHeaders(headers);
                                request.setBody(body);
                                return request.exchange();
                            } catch (Throwable ex) {
                                if (ex instanceof Exception) {
                                    throw (Exception) ex;
                                }
                                throw Throwables.wrapAsRuntimeException(ex);
                            }
                        }
                    });

                } catch (Exception ex) {
                    reject.handle(ex);
                    return null;
                }
            }
        });
    }
}

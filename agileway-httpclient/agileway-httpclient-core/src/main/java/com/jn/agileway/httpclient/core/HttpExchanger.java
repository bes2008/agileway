package com.jn.agileway.httpclient.core;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.exception.ErrorHandler;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.concurrent.promise.AsyncCallback;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.concurrent.promise.Task;
import com.jn.langx.util.function.Handler;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.uri.component.UriComponentsBuilder;
import com.jn.langx.util.retry.RetryConfig;
import com.jn.langx.util.retry.Retryer;

import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class HttpExchanger {

    private Executor executor;
    private HttpRequestFactory requestFactory;

    /**
     * 对请求进行拦截处理
     */
    private List<HttpRequestInterceptor> interceptors;

    /**
     * 请求转换器，主要是将 body进行转换，顺带补充 header等，只要一个转换成功就可以。
     */
    private List<HttpRequestTransformer> requestTransformers;

    public <I, O> Promise<HttpResponseEntity<O>> exchange(boolean async, HttpMethod method, String uriTemplate, Map<String, Object> uriVariables, HttpHeaders headers, I body, @Nullable final RetryConfig retryConfig) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uriTemplate);
        if (uriVariables != null) {
            uriBuilder.uriVariables(uriVariables);
        }
        URI uri = uriBuilder.build().toUri();
        return exchange(async, method, uri, headers, body, retryConfig);
    }

    public <I, O> Promise<HttpResponseEntity<O>> exchange(boolean async, @NonNull HttpMethod method, @NonNull URI uri, @Nullable HttpHeaders headers, I body, @Nullable final RetryConfig retryConfig) {
        Task<HttpResponse> sendHttpTask = new Task<HttpResponse>() {

            @Override
            public HttpResponse run(Handler<HttpResponse> resolve, ErrorHandler reject) {
                RetryConfig theRetryConfig = retryConfig;
                if (theRetryConfig == null) {
                    theRetryConfig = RetryConfig.noneRetryConfig();
                }

                Preconditions.checkNotNull(method, "http method is required");
                Preconditions.checkNotNull(uri, "http uri is required");

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

                            FilteringHttpRequest filteringHttpRequest = new FilteringHttpRequest(uri, method, headers, body);
                            for (HttpRequestInterceptor interceptor : interceptors) {
                                if (!interceptor.intercept(filteringHttpRequest)) {
                                    return null;
                                }
                            }
                            try {
                                Object bodyObj = filteringHttpRequest.getBody();
                                if (bodyObj == null) {
                                    filteringHttpRequest.getHeaders().setContentLength(0L);
                                } else {
                                    FilteringHttpRequest transformedHttpRequest = null;
                                    for (int i = 0; transformedHttpRequest == null && i < requestTransformers.size(); i++) {
                                        if (requestTransformers.get(i).canTransform(filteringHttpRequest)) {
                                            transformedHttpRequest = requestTransformers.get(i).transform(filteringHttpRequest);
                                        }
                                    }

                                    if (transformedHttpRequest != null) {
                                        filteringHttpRequest = transformedHttpRequest;
                                    }
                                }
                                if (bodyObj != null && bodyObj.getClass() != byte[].class) {
                                    throw new IllegalStateException("the http request body is not byte[]");
                                }

                                HttpRequest request = requestFactory.create(filteringHttpRequest.getMethod(), filteringHttpRequest.getUri());
                                request.setHeaders(filteringHttpRequest.getHeaders());

                                OutputStream out = request.getBody();

                                if (bodyObj != null) {
                                    byte[] body = (byte[]) bodyObj;
                                    IOs.write(body, out);
                                }
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
        };

        Promise<HttpResponse> promise = async ? new Promise<HttpResponse>(executor, sendHttpTask) : new Promise<HttpResponse>(sendHttpTask);

        return promise.then(new AsyncCallback<HttpResponse, HttpResponseEntity<O>>() {
            @Override
            public HttpResponseEntity<O> apply(HttpResponse httpResponse) {
                return null;
            }
        }).catchError(new AsyncCallback<Throwable, HttpResponseEntity<O>>() {
            @Override
            public HttpResponseEntity<O> apply(Throwable ex) {
                return null;
            }
        });
    }
}

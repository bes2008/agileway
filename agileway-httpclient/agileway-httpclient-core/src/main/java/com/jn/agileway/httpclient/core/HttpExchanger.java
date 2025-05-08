package com.jn.agileway.httpclient.core;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.exception.ErrorHandler;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.concurrent.promise.AsyncCallback;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.concurrent.promise.Task;
import com.jn.langx.util.function.Handler;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.uri.component.UriComponentsBuilder;
import com.jn.langx.util.retry.RetryConfig;
import com.jn.langx.util.retry.Retryer;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class HttpExchanger extends AbstractInitializable {

    private Executor executor;
    private HttpRequestFactory requestFactory;

    /**
     * 对请求进行拦截处理
     */
    private List<HttpRequestInterceptor> builtinInterceptors = Lists.asList(new ContentTypeInterceptor());
    private List<HttpRequestInterceptor> customInterceptors = Lists.newArrayList();
    private List<HttpRequestInterceptor> interceptors;
    /**
     * 请求转换器，主要是将 body进行转换，顺带补充 header等，只要一个转换成功就可以。
     */
    private List<HttpRequestBodyWriter> requestBodyWriters;

    @Override
    protected void doInit() throws InitializationException {
        List<HttpRequestInterceptor> interceptors = Lists.newArrayList();
        if (customInterceptors != null) {
            interceptors.addAll(customInterceptors);
        }
        interceptors.addAll(builtinInterceptors);
        this.interceptors = Lists.immutableList(interceptors);
    }

    public void addRequestInterceptor(HttpRequestInterceptor interceptor) {
        if (interceptor != null) {
            this.customInterceptors.add(interceptor);
        }
    }

    public <I, O> Promise<HttpResponseEntity<O>> exchange(boolean async, HttpMethod method, String uriTemplate, Map<String, Object> uriVariables, HttpHeaders headers, I body, @Nullable final RetryConfig retryConfig) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uriTemplate);
        if (uriVariables != null) {
            uriBuilder.uriVariables(uriVariables);
        }
        URI uri = uriBuilder.build().toUri();
        return exchange(async, method, uri, headers, body, retryConfig);
    }

    public <I, O> Promise<HttpResponseEntity<O>> exchange(boolean async, @NonNull HttpMethod method, @NonNull URI uri, @Nullable HttpHeaders headers, I body, @Nullable final RetryConfig retryConfig) {
        Task<HttpResponse> sendRequestTask = new Task<HttpResponse>() {

            @Override
            public HttpResponse run(Handler<HttpResponse> resolve, ErrorHandler reject) {
                RetryConfig theRetryConfig = retryConfig;
                if (theRetryConfig == null) {
                    theRetryConfig = RetryConfig.noneRetryConfig();
                }

                if (method == null) {
                    throw new InvalidHttpRequestException("http method is required");
                }
                if (uri == null) {
                    throw new InvalidHttpRequestException("http uri is required");
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

                            InterceptingHttpRequest interceptingHttpRequest = new InterceptingHttpRequest(uri, method, headers, body);

                            for (HttpRequestInterceptor interceptor : interceptors) {
                                interceptor.intercept(interceptingHttpRequest);
                            }

                            HttpRequest request = requestFactory.create(interceptingHttpRequest.getMethod(), interceptingHttpRequest.getUri(), interceptingHttpRequest.getHeaders().getContentType());
                            request.addHeaders(interceptingHttpRequest.getHeaders());

                            for (HttpRequestBodyWriter requestBodyWriter : requestBodyWriters) {
                                if (requestBodyWriter.canWrite(interceptingHttpRequest.getBody(), interceptingHttpRequest.getHeaders().getContentType())) {
                                    requestBodyWriter.write(interceptingHttpRequest.getBody(), interceptingHttpRequest.getHeaders().getContentType(), request);
                                    break;
                                }
                            }
                            try {
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

        Promise<HttpResponse> promise = async ? new Promise<HttpResponse>(executor, sendRequestTask) : new Promise<HttpResponse>(sendRequestTask);

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

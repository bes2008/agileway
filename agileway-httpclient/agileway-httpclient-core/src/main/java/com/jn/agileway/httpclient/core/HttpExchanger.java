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
    private UnderlyingHttpRequestFactory requestFactory;

    /**
     * 对请求进行拦截处理
     */
    private List<HttpRequestInterceptor> builtinRequestInterceptors = Lists.asList(new ContentTypeHttpRequestInterceptor());
    private List<HttpRequestInterceptor> customRequestInterceptors = Lists.newArrayList();
    private List<HttpRequestInterceptor> requestInterceptors;
    /**
     * 主要是将 body进行转换，顺带补充 header等，只要一个转换成功就可以。
     */
    private List<HttpRequestBodySerializer> requestBodySerializers;

    @Override
    protected void doInit() throws InitializationException {
        List<HttpRequestInterceptor> interceptors = Lists.newArrayList();
        if (customRequestInterceptors != null) {
            interceptors.addAll(customRequestInterceptors);
        }
        interceptors.addAll(builtinRequestInterceptors);
        this.requestInterceptors = Lists.immutableList(interceptors);
    }

    public void addRequestInterceptor(HttpRequestInterceptor interceptor) {
        if (interceptor != null) {
            this.customRequestInterceptors.add(interceptor);
        }
    }

    public <I, O> Promise<HttpResponse<O>> exchange(boolean async, HttpMethod method, String uriTemplate, Map<String, Object> uriVariables, HttpHeaders headers, I body, @Nullable final RetryConfig retryConfig) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uriTemplate);
        if (uriVariables != null) {
            uriBuilder.uriVariables(uriVariables);
        }
        URI uri = uriBuilder.build().toUri();
        return exchange(async, method, uri, headers, body, retryConfig);
    }

    public <I, O> Promise<HttpResponse<O>> exchange(boolean async, @NonNull HttpMethod method, @NonNull URI uri, @Nullable HttpHeaders headers, I body, @Nullable final RetryConfig retryConfig) {
        Task<UnderlyingHttpResponse> sendRequestTask = new Task<UnderlyingHttpResponse>() {

            @Override
            public UnderlyingHttpResponse run(Handler<UnderlyingHttpResponse> resolve, ErrorHandler reject) {
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
                    return Retryer.<UnderlyingHttpResponse>execute(new Predicate<Throwable>() {
                        @Override
                        public boolean test(Throwable ex) {
                            return false;
                        }
                    }, new Predicate<UnderlyingHttpResponse>() {
                        @Override
                        public boolean test(UnderlyingHttpResponse value) {
                            return false;
                        }
                    }, theRetryConfig, null, new Callable<UnderlyingHttpResponse>() {
                        @Override
                        public UnderlyingHttpResponse call() throws Exception {

                            HttpRequest interceptingHttpRequest = new HttpRequest(uri, method, headers, body);

                            for (HttpRequestInterceptor interceptor : requestInterceptors) {
                                interceptor.intercept(interceptingHttpRequest);
                            }

                            UnderlyingHttpRequest request = requestFactory.create(interceptingHttpRequest.getMethod(), interceptingHttpRequest.getUri(), interceptingHttpRequest.getHeaders().getContentType());
                            request.addHeaders(interceptingHttpRequest.getHeaders());

                            for (HttpRequestBodySerializer requestBodyWriter : requestBodySerializers) {
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

        Promise<UnderlyingHttpResponse> promise = async ? new Promise<UnderlyingHttpResponse>(executor, sendRequestTask) : new Promise<UnderlyingHttpResponse>(sendRequestTask);

        return promise.then(new AsyncCallback<UnderlyingHttpResponse, HttpResponse<O>>() {
            @Override
            public HttpResponse<O> apply(UnderlyingHttpResponse httpResponse) {
                return null;
            }
        }).catchError(new AsyncCallback<Throwable, HttpResponse<O>>() {
            @Override
            public HttpResponse<O> apply(Throwable ex) {
                return null;
            }
        });
    }
}

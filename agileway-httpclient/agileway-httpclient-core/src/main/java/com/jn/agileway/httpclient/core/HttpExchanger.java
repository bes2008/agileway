package com.jn.agileway.httpclient.core;

import com.jn.langx.annotation.Nullable;
import com.jn.langx.exception.ErrorHandler;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.concurrent.promise.Task;
import com.jn.langx.util.function.Handler;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.retry.RetryConfig;
import com.jn.langx.util.retry.Retryer;

import java.io.OutputStream;
import java.net.URI;
import java.util.List;
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
     * 请求转换器，主要是将 body进行转换，补充 header等，只要一个转换成功就可以。
     */
    private List<HttpRequestTransformer> requestTransformers;

    public <T> Promise<HttpResponse> exchange(URI uri, HttpMethod method, HttpHeaders headers, T body, @Nullable final RetryConfig retryConfig) {
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

                            FilteringHttpRequest filteringHttpRequest = new FilteringHttpRequest(uri, method, headers, body);
                            for (HttpRequestInterceptor interceptor : interceptors) {
                                if (!interceptor.intercept(filteringHttpRequest)) {
                                    return null;
                                }
                            }
                            try {

                                FilteringHttpRequest transformedHttpRequest = null;
                                for (int i = 0; transformedHttpRequest == null && i < requestTransformers.size(); i++) {
                                    if (requestTransformers.get(i).canTransform(filteringHttpRequest)) {
                                        transformedHttpRequest = requestTransformers.get(i).transform(filteringHttpRequest);
                                    }
                                }

                                if (transformedHttpRequest == null) {
                                    transformedHttpRequest = filteringHttpRequest;
                                }

                                Object bodyObj = transformedHttpRequest.getBody();
                                if (bodyObj != null && bodyObj.getClass() != byte[].class) {
                                    throw new IllegalStateException("the http request body is not byte[]");
                                }

                                HttpRequest request = requestFactory.create(transformedHttpRequest.getMethod(), transformedHttpRequest.getUri());
                                request.setHeaders(transformedHttpRequest.getHeaders());
                                OutputStream out = request.getBody();

                                if (out != null && bodyObj != null) {
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
        });
    }
}

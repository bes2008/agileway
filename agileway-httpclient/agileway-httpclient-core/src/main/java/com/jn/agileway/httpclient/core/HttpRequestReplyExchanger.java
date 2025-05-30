package com.jn.agileway.httpclient.core;

import com.jn.agileway.eipchannel.core.channel.pipe.PipedDuplexChannel;
import com.jn.agileway.eipchannel.core.endpoint.exchange.RequestReplyExchanger;
import com.jn.langx.exception.ErrorHandler;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.util.concurrent.promise.AsyncCallback;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.concurrent.promise.Task;
import com.jn.langx.util.function.Handler;

public class HttpRequestReplyExchanger extends AbstractLifecycle implements RequestReplyExchanger<HttpRequest<?>, HttpResponse<?>> {

    /**
     * <pre>
     *  OutboundChannel                             InboundChannel
     *
     *
     *  HttpRequest&lt;JavaBean>                   HttpResponse&lt;JavaBean>
     *        |                                             ^
     *        v                                             |
     *  ---------------------                       ---------------------
     *  |   Interceptors    |                       |   Interceptors    |
     *  ---------------------                       ---------------------
     *        |                                             ^
     *        v                                             |
     *  HttpRequest&lt;ByteArrayOutputStream>      HttpResponse&lt;ByteArrayInputStream>
     *        |                                             ^
     *        v                                             |
     *  ---------------------                       ---------------------
     *  |   Interceptors    |                       |   Interceptors    |
     *  ---------------------                       ---------------------
     *        |                                             ^
     *        v                                             |
     *  UnderlyingHttpRequest                      UnderlyingHttpResponse
     *        |                                             ^
     *        v                                             |
     * ---------------------------------------------------------------------------
     *         |------------> Transport Level ------------>|
     * ---------------------------------------------------------------------------
     * </pre>
     */
    private PipedDuplexChannel requestReplyChannel;
    private HttpExchangerConfiguration configuration;


    @Override
    public Promise<HttpResponse<?>> exchangeAsync(HttpRequest<?> request) {
        return exchangeInternal(true, request);
    }

    @Override
    public HttpResponse<?> exchange(HttpRequest<?> request) {
        return exchangeInternal(false, request).await();
    }

    private Promise<HttpResponse<?>> exchangeInternal(boolean async, HttpRequest<?> request) {

        Task<Boolean> sendRequestTask = new Task<Boolean>() {
            @Override
            public Boolean run(Handler<Boolean> handler, ErrorHandler errorHandler) {
                return requestReplyChannel.send(request);
            }
        };


        return (async ? new Promise<Boolean>(this.configuration.getExecutor(), sendRequestTask) : new Promise<Boolean>(sendRequestTask))
                .then(new AsyncCallback<Boolean, HttpResponse<?>>() {
                    @Override
                    public HttpResponse<?> apply(Boolean sent) {
                        if (sent) {
                            return (HttpResponse<?>) requestReplyChannel.poll();
                        }
                        return null;
                    }
                })
                .then(new AsyncCallback<HttpResponse<?>, HttpResponse<?>>() {
                    @Override
                    public HttpResponse<?> apply(HttpResponse<?> httpResponse) {
                        // statusCode >=400
                        return httpResponse;
                    }
                })
                .catchError(new AsyncCallback<Throwable, HttpResponse<?>>() {
                    @Override
                    public HttpResponse<?> apply(Throwable throwable) {
                        return null;
                    }
                });

    }
}

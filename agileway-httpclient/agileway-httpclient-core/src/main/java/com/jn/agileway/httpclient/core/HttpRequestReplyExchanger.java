package com.jn.agileway.httpclient.core;

import com.jn.agileway.eipchannel.core.channel.pipe.PipedDuplexChannel;
import com.jn.agileway.eipchannel.core.endpoint.exchange.RequestReplyExchanger;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.util.concurrent.promise.Promise;

public class HttpRequestReplyExchanger extends AbstractLifecycle implements RequestReplyExchanger {

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
     *        |
     *        v
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

    @Override
    public Promise<Message<?>> exchangeAsync(Message<?> request) {
        return null;
    }

    @Override
    public Message<?> exchange(Message<?> request) {
        return exchangeAsync(request).await();
    }
}

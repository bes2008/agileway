package com.jn.agileway.httpclient.core;

import com.jn.agileway.eipchannel.core.endpoint.exchange.RequestReplyExchanger;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.util.concurrent.promise.Promise;

public class HttpRequestReplyExchanger extends AbstractLifecycle implements RequestReplyExchanger {
    @Override
    public Promise<Message<?>> exchangeAsync(Message<?> request) {
        return null;
    }

    @Override
    public Message<?> exchange(Message<?> request) {
        return exchangeAsync(request).await();
    }
}

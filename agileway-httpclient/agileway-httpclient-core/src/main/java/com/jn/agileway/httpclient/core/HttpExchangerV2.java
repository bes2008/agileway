package com.jn.agileway.httpclient.core;

import com.jn.agileway.eipchannel.core.endpoint.exchange.RequestReplyExchanger;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.util.concurrent.promise.Promise;

public class HttpExchangerV2 extends AbstractLifecycle implements RequestReplyExchanger {
    private String name = getClass().getName();

    @Override
    public Promise<Message<?>> exchangeAsync(Message<?> request) {
        return null;
    }

    @Override
    public Message<?> exchange(Message<?> request) {
        return null;
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public String getName() {
        return this.name;
    }

}

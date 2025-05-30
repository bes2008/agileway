package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.eipchannel.core.channel.OutboundChannel;
import com.jn.agileway.eipchannel.core.channel.pipe.OutboundMessageInterceptor;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.agileway.httpclient.core.HttpRequest;

public class HttpRequestInterceptorAdapter extends OutboundMessageInterceptor {
    private HttpRequestInterceptor interceptor;

    public HttpRequestInterceptorAdapter(HttpRequestInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public Message<?> beforeOutbound(OutboundChannel channel, Message<?> message) {
        interceptor.intercept((HttpRequest) message);
        return message;
    }

    @Override
    public void afterOutbound(OutboundChannel channel, Message<?> message, boolean sent) {

    }
}

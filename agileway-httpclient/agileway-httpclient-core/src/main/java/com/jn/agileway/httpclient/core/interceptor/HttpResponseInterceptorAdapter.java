package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.eipchannel.core.channel.InboundChannel;
import com.jn.agileway.eipchannel.core.channel.pipe.InboundMessageInterceptor;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.agileway.httpclient.core.HttpResponse;

public class HttpResponseInterceptorAdapter extends InboundMessageInterceptor {
    private HttpResponseInterceptor interceptor;

    public HttpResponseInterceptorAdapter(HttpResponseInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public boolean beforeInbound(InboundChannel channel) {
        return true;
    }

    @Override
    public Message<?> afterInbound(InboundChannel channel, Message<?> message) {
        interceptor.intercept((HttpResponse) message);
        return message;
    }
}

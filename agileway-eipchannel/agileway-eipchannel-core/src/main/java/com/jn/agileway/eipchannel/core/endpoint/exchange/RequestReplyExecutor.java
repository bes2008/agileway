package com.jn.agileway.eipchannel.core.endpoint.exchange;

import com.jn.agileway.eipchannel.core.endpoint.sourcesink.sink.OutboundChannelSinker;
import com.jn.agileway.eipchannel.core.endpoint.sourcesink.source.InboundChannelMessageSource;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.lifecycle.AbstractLifecycle;

public abstract class RequestReplyExecutor extends AbstractLifecycle implements InboundChannelMessageSource, OutboundChannelSinker {

    private ThreadLocal<Message<?>> pollingMessageThreadLocal = new ThreadLocal<Message<?>>();

    public void setRequestMessage(Message<?> outboundMessage) {
        pollingMessageThreadLocal.set(outboundMessage);
    }

    protected Message<?> getRequestMessage() {
        return pollingMessageThreadLocal.get();
    }

    protected void clearRequestMessage() {
        pollingMessageThreadLocal.remove();
    }
}

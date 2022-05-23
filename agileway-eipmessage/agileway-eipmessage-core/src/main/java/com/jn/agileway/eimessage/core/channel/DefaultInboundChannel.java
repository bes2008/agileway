package com.jn.agileway.eimessage.core.channel;

import com.jn.agileway.eimessage.core.endpoint.sourcesink.source.InboundMessageSource;
import com.jn.agileway.eimessage.core.message.Message;


public class DefaultInboundChannel extends AbstractInboundChannel {
    private InboundMessageSource inboundMessageSource;

    @Override
    protected Message<?> pollInternal(long timeout) {
        return inboundMessageSource.poll(timeout);
    }

    public InboundMessageSource getInboundMessageSource() {
        return inboundMessageSource;
    }

    public void setInboundMessageSource(InboundMessageSource inboundMessageSource) {
        this.inboundMessageSource = inboundMessageSource;
    }
}

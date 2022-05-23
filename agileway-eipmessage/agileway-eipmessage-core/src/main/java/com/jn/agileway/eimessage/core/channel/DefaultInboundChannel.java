package com.jn.agileway.eimessage.core.channel;

import com.jn.agileway.eimessage.core.endpoint.sourcesink.source.InboundChannelMessageSource;
import com.jn.agileway.eimessage.core.message.Message;


public class DefaultInboundChannel extends AbstractInboundChannel {
    private InboundChannelMessageSource inboundMessageSource;

    @Override
    protected Message<?> pollInternal(long timeout) {
        return inboundMessageSource.poll(timeout);
    }

    public InboundChannelMessageSource getInboundMessageSource() {
        return inboundMessageSource;
    }

    public void setInboundMessageSource(InboundChannelMessageSource inboundMessageSource) {
        this.inboundMessageSource = inboundMessageSource;
    }
}

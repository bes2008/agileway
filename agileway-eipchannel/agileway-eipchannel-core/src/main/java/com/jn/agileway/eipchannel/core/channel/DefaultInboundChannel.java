package com.jn.agileway.eipchannel.core.channel;

import com.jn.agileway.eipchannel.core.endpoint.sourcesink.source.InboundChannelMessageSource;
import com.jn.agileway.eipchannel.core.message.Message;


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

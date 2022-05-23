package com.jn.agileway.eimessage.core.channel;

import com.jn.agileway.eimessage.core.endpoint.sourcesink.sink.OutboundChannelSinker;
import com.jn.agileway.eimessage.core.message.Message;

public class DefaultOutboundChannel extends AbstractOutboundChannel {
    private OutboundChannelSinker sinker;

    @Override
    protected boolean sendInternal(Message<?> message) {
        return this.sinker.sink(message);
    }

    public OutboundChannelSinker getSinker() {
        return sinker;
    }

    public void setSinker(OutboundChannelSinker sinker) {
        this.sinker = sinker;
    }

}

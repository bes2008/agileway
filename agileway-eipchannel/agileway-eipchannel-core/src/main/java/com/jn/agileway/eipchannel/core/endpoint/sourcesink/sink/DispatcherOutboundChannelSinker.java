package com.jn.agileway.eipchannel.core.endpoint.sourcesink.sink;

import com.jn.agileway.eipchannel.core.endpoint.dispatcher.MessageDispatcher;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.lifecycle.AbstractLifecycle;

public class DispatcherOutboundChannelSinker extends AbstractLifecycle implements OutboundChannelSinker {
    private MessageDispatcher dispatcher;

    @Override
    public boolean sink(Message<?> message) {
        return this.dispatcher.dispatch(message);
    }


    public MessageDispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}

package com.jn.agileway.eipchannel.core.endpoint.sourcesink.sink;

import com.jn.agileway.eipchannel.core.message.Message;

public interface OutboundChannelSinker {
    boolean sink(Message<?> message);
}

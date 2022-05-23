package com.jn.agileway.eimessage.core.endpoint.sourcesink.sink;

import com.jn.agileway.eimessage.core.message.Message;

public interface OutboundChannelSinker {
    boolean sink(Message<?> message);
}

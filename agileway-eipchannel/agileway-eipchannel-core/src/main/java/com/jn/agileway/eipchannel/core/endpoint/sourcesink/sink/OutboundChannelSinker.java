package com.jn.agileway.eipchannel.core.endpoint.sourcesink.sink;

import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.lifecycle.Lifecycle;

public interface OutboundChannelSinker extends Lifecycle {
    boolean sink(Message<?> message);
}

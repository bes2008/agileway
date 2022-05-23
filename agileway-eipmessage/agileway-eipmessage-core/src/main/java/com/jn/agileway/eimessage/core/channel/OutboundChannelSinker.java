package com.jn.agileway.eimessage.core.channel;

import com.jn.agileway.eimessage.core.message.Message;

public interface OutboundChannelSinker {
    boolean sink(Message<?> message);
}

package com.jn.agileway.eimessage.core.channel;

import com.jn.agileway.eimessage.core.Message;

import java.util.List;

public interface MessageRouter {
    List<OutboundChannel> route(List<OutboundChannel> channels, Message<?> message);
}

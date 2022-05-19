package com.jn.agileway.eimessage.core.channel;

public interface MulticastOutboundChannel extends OutboundChannel{
    MessageRouter getMessageRouter();
}

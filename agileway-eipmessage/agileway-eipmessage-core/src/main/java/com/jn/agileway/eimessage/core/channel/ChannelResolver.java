package com.jn.agileway.eimessage.core.channel;

public interface ChannelResolver {
    /**
     * Return the MessageChannel for the given name.
     */
    OutboundChannel resolveOutboundChannel(String channelName);

    InboundChannel resolveInboundChannel(String channelName);
}
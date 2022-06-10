package com.jn.agileway.eipchannel.core.channel;

public interface ChannelResolver {
    /**
     * Return the MessageChannel for the given name.
     */
    <C extends MessageChannel> C resolve(String channelName, ChannelDirect channelMode);
}
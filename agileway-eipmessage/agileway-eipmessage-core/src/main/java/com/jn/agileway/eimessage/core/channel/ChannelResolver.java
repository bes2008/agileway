package com.jn.agileway.eimessage.core.channel;

public interface ChannelResolver {
    /**
     * Return the MessageChannel for the given name.
     */
    <C extends MessageChannel> C resolve(String channelName, ChannelMode channelMode);
}
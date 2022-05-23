package com.jn.agileway.eipchannel.core.channel;

/**
 * 双工通道， 通常用于 Request, Replay模式
 */
public interface DuplexChannel extends InboundChannel, OutboundChannel {
}

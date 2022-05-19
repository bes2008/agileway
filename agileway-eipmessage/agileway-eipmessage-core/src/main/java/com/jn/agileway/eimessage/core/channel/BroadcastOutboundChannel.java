package com.jn.agileway.eimessage.core.channel;

/**
 * 用它来发消息时，可以把消息广播方式发到多个 outbound channel。
 * 用它来接收消息时，可以把接收到的消息在当前进程中广播出去。
 */
public interface BroadcastOutboundChannel extends MessageChannel {
}

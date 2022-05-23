package com.jn.agileway.eipchannel.core.channel;

import com.jn.agileway.eipchannel.core.message.Message;

/**
 * 只用于接收
 */
public interface InboundChannel extends MessageChannel {

    /**
     * Receive a message from this channel, blocking indefinitely if necessary.
     *
     * @return the next available {@link Message} or <code>null</code> if interrupted
     */
    Message<?> poll();

    /**
     * Receive a message from this channel, blocking until either a message is
     * available or the specified timeout period elapses.
     *
     * @param timeout the timeout in milliseconds
     * @return the next available {@link Message} or <code>null</code> if the
     * specified timeout period elapses or the message reception is interrupted
     */
    Message<?> poll(long timeout);

}

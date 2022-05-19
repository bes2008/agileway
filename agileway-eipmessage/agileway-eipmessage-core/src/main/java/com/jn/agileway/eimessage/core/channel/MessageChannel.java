package com.jn.agileway.eimessage.core.channel;


import com.jn.agileway.eimessage.core.Message;

/**
 * Base channel interface defining common behavior for sending messages.
 *
 */
public interface MessageChannel {

    /**
     * Send a {@link Message} to this channel. May throw a RuntimeException for
     * non-recoverable errors. Otherwise, if the Message cannot be sent for a
     * non-fatal reason this method will return 'false', and if the Message is
     * sent successfully, it will return 'true'.
     *
     * <p>Depending on the implementation, this method may block indefinitely.
     * To provide a maximum wait time, use {@link #send(Message, long)}.
     *
     * @param message the {@link Message} to send
     *
     * @return whether the Message has been sent successfully
     */
    boolean send(Message<?> message);

    /**
     * Send a message, blocking until either the message is accepted or the
     * specified timeout period elapses.
     *
     * @param message the {@link Message} to send
     * @param timeout the timeout in milliseconds
     *
     * @return <code>true</code> if the message is sent successfully,
     * <code>false</false> if the specified timeout period elapses or
     * the send is interrupted
     */
    boolean send(Message<?> message, long timeout);

}

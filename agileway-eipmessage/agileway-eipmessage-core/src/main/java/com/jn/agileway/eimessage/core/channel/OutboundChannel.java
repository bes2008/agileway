package com.jn.agileway.eimessage.core.channel;

import com.jn.agileway.eimessage.core.message.Message;

/**
 * 只用于发送
 */
public interface OutboundChannel extends MessageChannel {

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
     * @return whether the Message has been sent successfully
     */
    boolean send(Message<?> message);

    /**
     * @return 返回该channel绑定的数据类型
     * 如果返回值为null，则代表没有绑定到特定的数据类型
     */
    Class getDatatype();

    void setDataType(Class datatype);
}

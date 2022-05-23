package com.jn.agileway.eipchannel.core.channel.pipe;

import com.jn.agileway.eipchannel.core.channel.InboundChannel;
import com.jn.agileway.eipchannel.core.channel.OutboundChannel;
import com.jn.agileway.eipchannel.core.message.Message;

public interface ChannelMessageInterceptor {
    /**
     * @return 返回是否有可用的消息
     */
    boolean beforeInbound(InboundChannel channel);

    /**
     * 返回接收到的消息，此期间可以对接收到的消息做更改
     *
     * @param channel 通道
     * @param message 接收到的消息
     * @return 处理之后的消息
     */
    Message<?> afterInbound(InboundChannel channel, Message<?> message);

    /**
     * 发送之前执行，可以对message 做更改，如果返回为null ，则不会往下进行了
     */
    Message<?> beforeOutbound(OutboundChannel channel, Message<?> message);

    /**
     * 发送之后执行，可以对message 做更改，如果返回为null ，则不会往下进行了
     *
     * @param sent 是否发送成功
     */
    void afterOutbound(OutboundChannel channel, Message<?> message, boolean sent);
}

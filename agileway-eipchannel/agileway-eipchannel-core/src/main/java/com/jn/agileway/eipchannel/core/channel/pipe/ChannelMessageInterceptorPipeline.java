package com.jn.agileway.eipchannel.core.channel.pipe;

import com.jn.agileway.eipchannel.core.channel.InboundChannel;
import com.jn.agileway.eipchannel.core.channel.OutboundChannel;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.util.List;

public class ChannelMessageInterceptorPipeline {
    private static Logger logger = Loggers.getLogger(ChannelMessageInterceptorPipeline.class);
    private List<ChannelMessageInterceptor> interceptors = Collects.emptyArrayList();

    public void addFirst(ChannelMessageInterceptor interceptor) {
        interceptors.add(0, interceptor);
    }

    public void addLast(ChannelMessageInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    public Message<?> beforeOutbound(OutboundChannel channel, Message<?> message) {
        if (logger.isDebugEnabled()) {
            logger.debug("beforeOutbound on channel '{}', message: {}", channel, message);
        }
        for (ChannelMessageInterceptor interceptor : interceptors) {
            message = interceptor.beforeOutbound(channel, message);
            if (message == null) {
                return null;
            }
        }
        return message;
    }

    public void afterOutbound(OutboundChannel channel, Message<?> message, boolean sent) {
        if (logger.isDebugEnabled()) {
            logger.debug("afterOutbound (sent={}) on channel '{}', message: {}", sent, channel, message);
        }
        for (ChannelMessageInterceptor interceptor : interceptors) {
            interceptor.afterOutbound(channel, message, sent);
        }
    }

    public boolean beforeInbound(InboundChannel channel) {
        if (logger.isTraceEnabled()) {
            logger.trace("beforeInbound on channel '{}'", channel);
        }
        for (ChannelMessageInterceptor interceptor : interceptors) {
            if (!interceptor.beforeInbound(channel)) {
                return false;
            }
        }
        return true;
    }

    public Message<?> afterInbound(InboundChannel channel, Message<?> message) {
        if (message != null && logger.isDebugEnabled()) {
            logger.debug("afterInbound on channel '{}', message: {}", channel, message);
        }
        for (ChannelMessageInterceptor interceptor : interceptors) {
            message = interceptor.afterInbound(channel, message);
            if (message == null) {
                return null;
            }
        }
        return message;
    }
}

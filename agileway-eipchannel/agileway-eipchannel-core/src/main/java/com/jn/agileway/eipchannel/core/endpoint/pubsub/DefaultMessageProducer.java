package com.jn.agileway.eipchannel.core.endpoint.pubsub;


import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.agileway.eipchannel.core.channel.OutboundChannel;
import com.jn.agileway.eipchannel.core.endpoint.mapper.MessageMapper;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;

/**
 * producer -> channel -> dispatcher
 */
public class DefaultMessageProducer<T> extends AbstractMessagePubSubEndpoint<T> implements MessageProducer<T> {
    protected Logger logger = Loggers.getLogger(getClass());
    private OutboundChannel outboundChannel;

    @Override
    protected void doStart() {
        outboundChannel.startup();
    }

    @Override
    protected void doStop() {
        outboundChannel.shutdown();
    }

    public boolean send(T obj) {
        Preconditions.checkNotNull(obj);
        MessageMapper<T> messageMapper = getMessageMapper();
        Message<?> message = null;
        if (messageMapper != null) {
            message = messageMapper.map(obj);
        }
        if (message == null) {
            logger.warn("unsupported class: {}", Reflects.getFQNClassName(obj.getClass()));
        }
        return outboundChannel.send(message);
    }

    @Override
    public OutboundChannel getOutboundChannel() {
        return this.outboundChannel;
    }

    @Override
    public void setOutboundChannel(OutboundChannel channel) {
        this.outboundChannel = channel;
    }
}

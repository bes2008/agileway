package com.jn.agileway.eipchannel.core.endpoint.pubsub;

import com.jn.agileway.eipchannel.core.channel.InboundChannel;
import com.jn.agileway.eipchannel.core.endpoint.mapper.MessageMapper;
import com.jn.agileway.eipchannel.core.message.Message;

public class DefaultMessageConsumer extends AbstractMessagePubSubEndpoint implements MessageConsumer {
    private InboundChannel inboundChannel;
    private MessageMapper messageMapper;

    @Override
    public MessageMapper getMessageMapper() {
        return messageMapper;
    }

    @Override
    public void setMessageMapper(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    protected void doStart() {
        inboundChannel.startup();
    }

    @Override
    protected void doStop() {
        inboundChannel.shutdown();
    }

    public Object poll(long timeout) {
        Message<?> message =  this.inboundChannel.poll(timeout);
        Object msg = message;
        if(message!=null && messageMapper!=null){
            msg = messageMapper.reverseMap(message);
        }
        return msg;
    }

    @Override
    public Object poll() {
        return poll(-1);
    }

    @Override
    public InboundChannel getInboundChannel() {
        return this.inboundChannel;
    }

    @Override
    public void setInboundChannel(InboundChannel channel) {
        this.inboundChannel = channel;
    }
}

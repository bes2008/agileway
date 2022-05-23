package com.jn.agileway.eipchannel.eventchannel.sink;

import com.jn.agileway.eipchannel.core.endpoint.mapper.MessageMapper;
import com.jn.agileway.eipchannel.core.endpoint.sourcesink.sink.OutboundChannelSinker;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.event.DomainEvent;
import com.jn.langx.event.EventPublisher;
import com.jn.langx.event.EventPublisherAware;

public class EventOutboundLocalSinker implements OutboundChannelSinker, EventPublisherAware {
    private EventPublisher localPublisher;
    private MessageMapper<DomainEvent> messageMapper;

    @Override
    public boolean sink(Message<?> message) {
        DomainEvent event = messageMapper.reverseMap(message);
        this.localPublisher.publish(event);
        return true;
    }

    @Override
    public EventPublisher getEventPublisher() {
        return this.localPublisher;
    }

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        this.localPublisher = eventPublisher;
    }
}

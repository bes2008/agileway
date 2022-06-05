package com.jn.agileway.eipchannel.eventchannel.sink;

import com.jn.agileway.eipchannel.core.endpoint.sourcesink.sink.OutboundChannelSinker;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.agileway.eipchannel.eventchannel.mapper.DomainEventMapper;
import com.jn.langx.event.DomainEvent;
import com.jn.langx.event.EventPublisher;
import com.jn.langx.event.EventPublisherAware;
import com.jn.langx.lifecycle.AbstractLifecycle;

public class EventOutboundLocalSinker extends AbstractLifecycle implements OutboundChannelSinker, EventPublisherAware {
    private EventPublisher localPublisher;
    private DomainEventMapper domainEventMapper;
    @Override
    public boolean sink(Message<?> message) {
        DomainEvent event = domainEventMapper.reverseMap(message);
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

    public DomainEventMapper getDomainEventMapper() {
        return domainEventMapper;
    }

    public void setDomainEventMapper(DomainEventMapper domainEventMapper) {
        this.domainEventMapper = domainEventMapper;
    }
}

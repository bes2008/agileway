package com.jn.agileway.eipchannel.eventchannel;

import com.jn.agileway.eipchannel.core.endpoint.pubsub.DefaultMessageProducer;
import com.jn.agileway.eipchannel.eventchannel.mapper.DomainEventMapper;
import com.jn.langx.event.DomainEvent;

public class EventProducer extends DefaultMessageProducer<DomainEvent> {
    public void setDomainEventMapper(DomainEventMapper mapper) {
        this.setMessageMapper(mapper);
    }
}

package com.jn.agileway.eipchannel.eventchannel.mapper;

import com.jn.agileway.eipchannel.core.endpoint.mapper.MessageMapper;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.event.DomainEvent;

public interface DomainEventMapper extends MessageMapper<DomainEvent> {
    @Override
    Message<?> map(DomainEvent domainEvent);

    @Override
    DomainEvent reverseMap(Message<?> message);
}

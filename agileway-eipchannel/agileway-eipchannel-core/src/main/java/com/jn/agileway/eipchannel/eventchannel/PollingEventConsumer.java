package com.jn.agileway.eipchannel.eventchannel;

import com.jn.agileway.eipchannel.core.channel.DefaultOutboundChannel;
import com.jn.agileway.eipchannel.core.endpoint.pubsub.PollingConsumer;
import com.jn.agileway.eipchannel.core.router.DirectOutboundRouter;
import com.jn.agileway.eipchannel.core.router.MessageRouter;
import com.jn.agileway.eipchannel.eventchannel.mapper.DomainEventMapper;
import com.jn.agileway.eipchannel.eventchannel.sink.EventOutboundLocalSinker;
import com.jn.langx.event.EventPublisher;

import java.util.concurrent.Executor;

public class PollingEventConsumer extends PollingConsumer {

    public PollingEventConsumer(
            Executor pollTaskExecutor,
            EventPublisher eventPublisher,
            DomainEventMapper domainEventMapper){
        // event sink
        EventOutboundLocalSinker sinker = new EventOutboundLocalSinker();
        sinker.setEventPublisher(eventPublisher);
        sinker.setDomainEventMapper(domainEventMapper);

        // outbound
        DefaultOutboundChannel outboundChannel = new DefaultOutboundChannel();
        outboundChannel.setSinker(sinker);

        // router
        MessageRouter router = new DirectOutboundRouter(outboundChannel);

        setMessageHandler(router);
        setPollAndConsumeExecutor(pollTaskExecutor);
    }
}

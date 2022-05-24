package com.jn.agileway.eipchannel.eventchannel;

import com.jn.agileway.eipchannel.core.channel.DefaultInboundChannel;
import com.jn.agileway.eipchannel.core.channel.DefaultOutboundChannel;
import com.jn.agileway.eipchannel.core.channel.pipe.ChannelMessageInterceptorPipeline;
import com.jn.agileway.eipchannel.core.endpoint.pubsub.PollingConsumer;
import com.jn.agileway.eipchannel.core.endpoint.sourcesink.source.InboundChannelMessageSource;
import com.jn.agileway.eipchannel.core.router.DirectOutboundRouter;
import com.jn.agileway.eipchannel.core.router.MessageRouter;
import com.jn.agileway.eipchannel.eventchannel.mapper.DomainEventMapper;
import com.jn.agileway.eipchannel.eventchannel.sink.EventOutboundLocalSinker;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.event.EventPublisher;
import com.jn.langx.util.Preconditions;

import java.util.concurrent.Executor;

public class PollingEventConsumer extends PollingConsumer {

    public PollingEventConsumer(
            @NotEmpty
                    String name,
            @NonNull
                    Executor messageChannelExecutor,
            @NonNull
                    EventPublisher eventPublisher,
            @NonNull
                    DomainEventMapper domainEventMapper,
            @NonNull
                    InboundChannelMessageSource inboundChannelMessageSource,
            @Nullable
                    ChannelMessageInterceptorPipeline inboundPipeline,
            @Nullable
                    ChannelMessageInterceptorPipeline outboundPipeline
    ) {
        Preconditions.checkNotNull(messageChannelExecutor, "executor is required");
        Preconditions.checkNotNull(inboundChannelMessageSource, "inboundChannelMessageSource is required");
        Preconditions.checkNotNull(eventPublisher, "the local event publisher is required");
        Preconditions.checkNotNull(domainEventMapper, "domain event mapper is required");
        Preconditions.checkNotEmpty(name, "name is required");

        setName(name);
        // event sink
        EventOutboundLocalSinker sinker = new EventOutboundLocalSinker();
        sinker.setEventPublisher(eventPublisher);
        sinker.setDomainEventMapper(domainEventMapper);

        // outbound
        DefaultOutboundChannel outboundChannel = new DefaultOutboundChannel();
        if (outboundPipeline != null) {
            outboundChannel.setPipeline(outboundPipeline);
        }
        outboundChannel.setSinker(sinker);
        outboundChannel.setName(name + "-outbound-channel");

        // router
        MessageRouter router = new DirectOutboundRouter(outboundChannel);

        setMessageHandler(router);

        DefaultInboundChannel inboundChannel = new DefaultInboundChannel();
        inboundChannel.setInboundMessageSource(inboundChannelMessageSource);
        if (inboundPipeline != null) {
            inboundChannel.setPipeline(inboundPipeline);
        }
        outboundChannel.setName(name + "-inbound-channel");

        setInboundChannel(inboundChannel);
        setChannelExecutor(messageChannelExecutor);
    }
}

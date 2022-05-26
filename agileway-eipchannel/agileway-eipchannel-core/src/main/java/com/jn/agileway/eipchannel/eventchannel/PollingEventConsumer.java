package com.jn.agileway.eipchannel.eventchannel;

import com.jn.agileway.eipchannel.core.channel.PipedInboundChannel;
import com.jn.agileway.eipchannel.core.channel.PipedOutboundChannel;
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
import com.jn.langx.lifecycle.AbstractLifecycle;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.timing.scheduling.ImmediateTrigger;
import com.jn.langx.util.timing.scheduling.ScheduledExecutors;
import com.jn.langx.util.timing.scheduling.Trigger;
import com.jn.langx.util.timing.timer.Timer;
import com.jn.langx.util.timing.timer.scheduled.ScheduledExecutorTimer;

import java.util.concurrent.Executor;

/**
 * @since 3.1.0
 */
public class PollingEventConsumer extends AbstractLifecycle {
    private PollingConsumer consumer;

    /**
     * @since 3.1.0
     */
    public PollingEventConsumer(
            @NotEmpty
                    String name,
            @Nullable Trigger trigger,
            @NonNull Executor channelExecutor,
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
        this(name,
                new ScheduledExecutorTimer(ScheduledExecutors.getScheduledExecutor(), channelExecutor),
                trigger,
                eventPublisher,
                domainEventMapper,
                inboundChannelMessageSource,
                inboundPipeline,
                outboundPipeline);
    }


    /**
     * @since 3.1.1
     */
    public PollingEventConsumer(
            @NotEmpty
                    String name,
            @Nullable Trigger trigger,
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
        this(name, (Timer) null, trigger, eventPublisher, domainEventMapper, inboundChannelMessageSource, inboundPipeline, outboundPipeline);
    }


    /**
     * @since 3.1.1
     */
    public PollingEventConsumer(
            @NotEmpty
                    String name,
            @NonNull Timer timer,
            @Nullable Trigger trigger,
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
        Preconditions.checkNotNull(inboundChannelMessageSource, "inboundChannelMessageSource is required");
        Preconditions.checkNotNull(eventPublisher, "the local event publisher is required");
        Preconditions.checkNotNull(domainEventMapper, "domain event mapper is required");
        Preconditions.checkNotEmpty(name, "name is required");

        PollingConsumer delegate = new PollingConsumer();
        delegate.setName(name);
        // event sink
        EventOutboundLocalSinker sinker = new EventOutboundLocalSinker();
        sinker.setEventPublisher(eventPublisher);
        sinker.setDomainEventMapper(domainEventMapper);

        // outbound
        PipedOutboundChannel outboundChannel = new PipedOutboundChannel();
        if (outboundPipeline != null) {
            outboundChannel.setPipeline(outboundPipeline);
        }
        outboundChannel.setSinker(sinker);
        outboundChannel.setName(name + "-outbound-channel");

        // router
        MessageRouter router = new DirectOutboundRouter(outboundChannel);

        delegate.setMessageHandler(router);

        PipedInboundChannel inboundChannel = new PipedInboundChannel();
        inboundChannel.setInboundMessageSource(inboundChannelMessageSource);
        if (inboundPipeline != null) {
            inboundChannel.setPipeline(inboundPipeline);
        }
        inboundChannel.setName(name + "-inbound-channel");
        delegate.setInboundChannel(inboundChannel);

        if (trigger == null) {
            trigger = new ImmediateTrigger();
        }
        delegate.setTrigger(trigger);
        delegate.setTimer(timer);

        this.consumer = delegate;
    }

    @Override
    protected void doStart() {
        this.consumer.startup();
    }

    @Override
    protected void doStop() {
        this.consumer.shutdown();
    }
}

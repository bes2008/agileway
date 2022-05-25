package com.jn.agileway.eipchannel.tests;

import com.jn.agileway.eipchannel.core.channel.DefaultOutboundChannel;
import com.jn.agileway.eipchannel.core.endpoint.sourcesink.sink.BlockingQueueOutboundChannelSinker;
import com.jn.agileway.eipchannel.core.endpoint.sourcesink.source.BlockingQueueInboundChannelMessageSource;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.agileway.eipchannel.core.message.MessageBuilder;
import com.jn.agileway.eipchannel.eventchannel.EventProducer;
import com.jn.agileway.eipchannel.eventchannel.PollingEventConsumer;
import com.jn.agileway.eipchannel.eventchannel.mapper.DomainEventMapper;
import com.jn.langx.event.DomainEvent;
import com.jn.langx.event.EventListener;
import com.jn.langx.event.EventPublisher;
import com.jn.langx.event.local.SimpleEventPublisher;
import com.jn.langx.exception.ErrorHandler;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.concurrent.executor.ScheduledExecutors;
import com.jn.langx.util.enums.Enums;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.timing.scheduling.PeriodicTrigger;
import com.jn.langx.util.timing.scheduling.Trigger;
import com.jn.langx.util.timing.timer.Timer;
import com.jn.langx.util.timing.timer.WheelTimers;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.concurrent.*;

public class QueueChannelTests {
    private static final Logger logger = Loggers.getLogger(QueueChannelTests.class);

    @Test
    public void test() throws Throwable{
        final String domain = "user";

        DomainEventMapper eventMapper = new DomainEventMapper() {
            @Override
            public Message<?> map(DomainEvent domainEvent) {
                return MessageBuilder.withPayload(domainEvent).build();
            }

            @Override
            public DomainEvent reverseMap(Message<?> message) {
                return (DomainEvent) message.getPayload();
            }
        };

        BlockingQueue queue = new LinkedBlockingQueue<DomainEvent>();

        // producer
        BlockingQueueOutboundChannelSinker queueOutboundChannelSinker = new BlockingQueueOutboundChannelSinker();
        queueOutboundChannelSinker.setQueue(queue);
        DefaultOutboundChannel producerOutboundChannel = new DefaultOutboundChannel();
        producerOutboundChannel.setName("producer-event-outbound-channel");
        producerOutboundChannel.setSinker(queueOutboundChannelSinker);
        final EventProducer eventProducer = new EventProducer();
        eventProducer.setName("event-producer");
        eventProducer.setDomainEventMapper(eventMapper);
        eventProducer.setOutboundChannel(producerOutboundChannel);
        eventProducer.startup();


        // 生产者每 一秒投递一个消息到queue
        ScheduledExecutors.scheduleTask(new Runnable() {
            @Override
            public void run() {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                int r = random.nextInt(1000);
                int userEventType = r % 3;
                UserEvent userEvent = new UserEvent(domain, "user-" + r);
                Enums.ofCode(UserEventType.class, userEventType);
                eventProducer.send(userEvent);
            }
        }, new PeriodicTrigger(1000), new ErrorHandler() {
            @Override
            public void handle(Throwable throwable) {
                logger.warn(throwable.getMessage(), throwable);
            }
        });


        BlockingQueueInboundChannelMessageSource inboundChannelMessageSource = new BlockingQueueInboundChannelMessageSource();
        inboundChannelMessageSource.setQueue(queue);
        BlockingQueue eventProcessTaskQueue = new LinkedBlockingQueue();
        EventPublisher publisher = new SimpleEventPublisher();
        publisher.addEventListener(domain, new EventListener() {
            @Override
            public void on(DomainEvent event) {
                System.out.println(StringTemplates.formatWithPlaceholder("consume {} event: {}", event.getDomain(), event));
            }
        });

        Executor messageChannelExecutor = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS, eventProcessTaskQueue);
        // consumer
        PollingEventConsumer consumer = new PollingEventConsumer("event-consumer",
                (Trigger) null,
                messageChannelExecutor,
                publisher,
                eventMapper,
                inboundChannelMessageSource,
                null, null
        );
        consumer.startup();

        Thread.sleep(12000);
        eventProducer.shutdown();
        consumer.shutdown();
    }
}

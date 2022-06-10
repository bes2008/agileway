package com.jn.agileway.dmmq.core;

import com.jn.agileway.dmmq.core.allocator.DefaultTopicAllocator;
import com.jn.langx.lifecycle.*;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Functions;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class MessageTopic<M> extends AbstractLifecycle implements Destroyable {
    private String name = DefaultTopicAllocator.TOPIC_DEFAULT;
    private Disruptor<MessageHolder<M>> disruptor;
    private MessageTopicConfiguration configuration;
    private final MessageHolderFactory<M> messageHolderFactory = new MessageHolderFactory<M>();
    private final ConcurrentHashMap<String, Consumer<M>> consumerMap = new ConcurrentHashMap<String, Consumer<M>>();

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MessageTopicConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(MessageTopicConfiguration configuration) {
        this.configuration = configuration;
    }

    public void subscribe(Consumer<M> consumer, String... dependencies) {
        if (Emptys.isNotEmpty(dependencies)) {
            Consumer[] dependencyConsumers = (Consumer[]) Pipeline.of(dependencies).map(new Function<String, Consumer<M>>() {
                @Override
                public Consumer<M> apply(String dependencyConsumerName) {
                    return consumerMap.get(dependencyConsumerName);
                }
            }).filter(Functions.<Consumer<M>>nonNullPredicate()).toArray();
            disruptor.after(dependencyConsumers).then(consumer);
        } else {
            disruptor.handleEventsWith(consumer);
        }
        consumerMap.put(consumer.getName(), consumer);
    }

    @Override
    public void doStart() {
        disruptor.start();
    }

    @Override
    public void doStop() {
        try {
            disruptor.shutdown(20L, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            // ignore
        }
    }

    @Override
    public void destroy() {
        shutdown();
    }


    @Override
    public void doInit() {
        if (configuration.getWaitStrategy() != null) {
            disruptor = new Disruptor<MessageHolder<M>>(messageHolderFactory,
                    configuration.getRingBufferSize(),
                    configuration.getExecutor(),
                    configuration.getProducerType(),
                    configuration.getWaitStrategy());
        } else {
            disruptor = new Disruptor<MessageHolder<M>>(messageHolderFactory,
                    configuration.getRingBufferSize(),
                    configuration.getExecutor());
        }
    }

    public void publish(M message) {
        MessageTranslator translator = configuration.getMessageTranslator();
        disruptor.publishEvent(translator, getName(), message);
    }
}

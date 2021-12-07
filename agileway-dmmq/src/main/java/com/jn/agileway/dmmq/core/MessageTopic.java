package com.jn.agileway.dmmq.core;

import com.jn.agileway.dmmq.core.allocator.DefaultTopicAllocator;
import com.jn.langx.lifecycle.Destroyable;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.lifecycle.Lifecycle;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.logging.Loggers;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class MessageTopic<M> implements Destroyable, Initializable, Lifecycle {
    private static final Logger logger = Loggers.getLogger(MessageTopic.class);
    private String name = DefaultTopicAllocator.TOPIC_DEFAULT;
    private Disruptor<MessageHolder<M>> disruptor;
    private MessageTopicConfiguration configuration;
    private volatile boolean running = false;
    private final MessageHolderFactory<M> messageHolderFactory = new MessageHolderFactory<M>();
    private final ConcurrentHashMap<String, Consumer<M>> consumerMap = new ConcurrentHashMap<String, Consumer<M>>();
    private boolean inited = false;

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
    public void startup() {
        if (!inited) {
            init();
        }
        running = true;
        disruptor.start();
    }

    @Override
    public void shutdown() {
        if (running) {
            running = false;
            try {
                disruptor.shutdown(20L, TimeUnit.SECONDS);
            } catch (TimeoutException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void destroy() {
        shutdown();
    }


    @Override
    public void init() throws InitializationException {
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
        inited = true;
    }

    public void publish(M message) {
        MessageTranslator translator = configuration.getMessageTranslator();
        translator.setMessage(message);
        translator.setTopicName(getName());
        disruptor.publishEvent(translator);
    }
}

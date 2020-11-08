package com.jn.agileway.dmmq.disruptor;

import com.jn.agileway.dmmq.core.Consumer;
import com.jn.agileway.dmmq.core.MessageHolder;
import com.jn.agileway.dmmq.core.MessageTopic;
import com.jn.agileway.dmmq.core.MessageTopicConfiguration;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Functions;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class DisruptorMessageTopic<M> extends MessageTopic<M> {
    private static final Logger logger = LoggerFactory.getLogger(DisruptorMessageTopic.class);
    private Disruptor<MessageHolder<M>> disruptor;
    private final DisruptorMessageHolderFactory<M> messageHolderFactory = new DisruptorMessageHolderFactory<M>();

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DisruptorTopicConfiguration getDisruptorTopicConfiguration() {
        return (DisruptorTopicConfiguration) getConfiguration();
    }

    public void setConfiguration(MessageTopicConfiguration configuration) {
        if (configuration instanceof DisruptorTopicConfiguration) {
            super.setConfiguration(configuration);
        }
    }

    public void subscribe(DisruptorConsumer<M> consumer, String... dependencies) {
        if (Emptys.isNotEmpty(dependencies)) {
            DisruptorConsumer[] dependencyConsumers = (DisruptorConsumer[]) Pipeline.of(dependencies).map(new Function<String, Consumer<M>>() {
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
        final DisruptorTopicConfiguration configuration = getDisruptorTopicConfiguration();
        if (configuration.getWaitStrategy() != null) {
            disruptor = new Disruptor<MessageHolder<M>>(messageHolderFactory,
                    configuration.getBufferSize(),
                    configuration.getExecutor(),
                    configuration.getProducerType(),
                    configuration.getWaitStrategy());
        } else {
            disruptor = new Disruptor<MessageHolder<M>>(messageHolderFactory,
                    configuration.getBufferSize(),
                    configuration.getExecutor());
        }
        inited = true;
    }

    public void publish(@NonNull M message) {
        DisruptorMessageTranslator translator = getDisruptorTopicConfiguration().getMessageTranslator();
        translator.setMessage(message);
        translator.setTopicName(getName());
        disruptor.publishEvent(translator);
    }
}

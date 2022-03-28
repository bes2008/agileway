package com.jn.agileway.dmmq.core;

import com.jn.agileway.dmmq.core.event.TopicEvent;
import com.jn.agileway.dmmq.core.event.TopicEventType;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.event.EventPublisher;
import com.jn.langx.lifecycle.Lifecycle;
import com.jn.langx.registry.GenericRegistry;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;


public class MessageTopicDispatcher extends GenericRegistry<MessageTopic> implements Lifecycle {
    private static final Logger logger = Loggers.getLogger(MessageTopicDispatcher.class);
    private EventPublisher<TopicEvent> topicEventPublisher;
    private volatile boolean running = false;

    public MessageTopicDispatcher() {
    }

    public EventPublisher<TopicEvent> getTopicEventPublisher() {
        return topicEventPublisher;
    }

    public void setTopicEventPublisher(EventPublisher<TopicEvent> topicEventPublisher) {
        this.topicEventPublisher = topicEventPublisher;
    }

    /**
     *
     * @param messageTopic
     * @see #register(MessageTopic)
     */
    @Deprecated
    public void registerTopic(@NonNull MessageTopic messageTopic) {
        Preconditions.checkNotNull(messageTopic);
        super.register(messageTopic.getName(), messageTopic);
        topicEventPublisher.publish(new TopicEvent(messageTopic, TopicEventType.ADD));
    }

    @Override
    public void register(MessageTopic messageTopic) {
        registerTopic(messageTopic);
    }

    @Override
    public void register(String name, MessageTopic messageTopic) {
        messageTopic.setName(name);
        register(messageTopic);
    }


    @Deprecated
    public void unregisterTopic(String name) {
        Preconditions.checkNotNull(name);
        MessageTopic topic = registry.remove(name);
        if (topic != null) {
            topicEventPublisher.publish(new TopicEvent(topic, TopicEventType.REMOVE));
        }
    }

    public void publish(String topicName, final Object message) {
        if (!running) {
            logger.warn("Publish message to topic {} fail, the message topic dispatcher is not running", topicName);
        }
        if ("*".equals(topicName)) {
            Collects.forEach(registry, new Consumer2<String, MessageTopic>() {
                @Override
                public void accept(String key, MessageTopic topic) {
                    topic.publish(message);
                }
            });
        }

        MessageTopic topic = registry.get(topicName);
        if (Objs.isNull(topic)) {
            logger.warn("Can't find the specified topic : {}", topicName);
        } else {
            topic.publish(message);
        }
    }

    public <M> void subscribe(String topicName, final Consumer<M> consumer, final String... dependencies) {
        if ("*".equals(topicName)) {
            Collects.forEach(registry, new Consumer2<String, MessageTopic>() {
                @Override
                public void accept(String key, MessageTopic topic) {
                    topic.subscribe(consumer, dependencies);
                }
            });
        } else {
            MessageTopic topic = registry.get(topicName);
            if (Objs.isNull(topic)) {
                logger.warn("Can't find a topic : {}", topicName);
            } else {
                topic.subscribe(consumer, dependencies);
            }
        }
    }


    @Override
    public void startup() {
        if (!running) {
            Collects.forEach(registry, new Consumer2<String, MessageTopic>() {
                @Override
                public void accept(String key, MessageTopic value) {
                    value.startup();
                }
            });
            running = true;
        }
    }

    @Override
    public void shutdown() {
        running = false;
        Collects.forEach(registry, new Consumer2<String, MessageTopic>() {
            @Override
            public void accept(String key, MessageTopic value) {
                try {
                    value.shutdown();
                } catch (Throwable ex) {
                    logger.error("error:{}, stack:{}", ex.getMessage(), ex);
                }
            }
        });
    }
}

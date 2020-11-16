package com.jn.agileway.dmmq.core;

import com.jn.agileway.dmmq.core.event.TopicEvent;
import com.jn.agileway.dmmq.core.event.TopicEventType;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.event.EventPublisher;
import com.jn.langx.lifecycle.Lifecycle;
import com.jn.langx.registry.Registry;
import com.jn.langx.util.Objects;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class MessageTopicDispatcher implements Lifecycle, Registry<String,MessageTopic> {
    private static final Logger logger = LoggerFactory.getLogger(MessageTopicDispatcher.class);
    private final Map<String, MessageTopic> topicMap = Collects.emptyHashMap();
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

    public List<String> getTopicNames() {
        return Collects.newArrayList(topicMap.keySet());
    }

    /**
     *
     * @param messageTopic
     * @see #register(MessageTopic)
     */
    @Deprecated
    public void registerTopic(@NonNull MessageTopic messageTopic) {
        Preconditions.checkNotNull(messageTopic);
        topicMap.put(messageTopic.getName(), messageTopic);
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

    @Override
    public MessageTopic get(String name) {
        return topicMap.get(name);
    }

    public void unregisterTopic(String name) {
        Preconditions.checkNotNull(name);
        MessageTopic topic = topicMap.remove(name);
        if (topic != null) {
            topicEventPublisher.publish(new TopicEvent(topic, TopicEventType.REMOVE));
        }
    }

    public void publish(String topicName, final Object message) {
        if (!running) {
            logger.warn("Publish message to topic {} fail, the message topic dispatcher is not running", topicName);
        }
        if ("*".equals(topicName)) {
            Collects.forEach(topicMap, new Consumer2<String, MessageTopic>() {
                @Override
                public void accept(String key, MessageTopic topic) {
                    topic.publish(message);
                }
            });
        }

        MessageTopic topic = topicMap.get(topicName);
        if (Objects.isNull(topic)) {
            logger.warn("Can't find the specified topic : {}", topicName);
        } else {
            topic.publish(message);
        }
    }

    public <M> void subscribe(String topicName, final Consumer<M> consumer, final String... dependencies) {
        if ("*".equals(topicName)) {
            Collects.forEach(topicMap, new Consumer2<String, MessageTopic>() {
                @Override
                public void accept(String key, MessageTopic topic) {
                    topic.subscribe(consumer, dependencies);
                }
            });
        } else {
            MessageTopic topic = topicMap.get(topicName);
            if (Objects.isNull(topic)) {
                logger.warn("Can't find a topic : {}", topicName);
            } else {
                topic.subscribe(consumer, dependencies);
            }
        }
    }


    @Override
    public void startup() {
        if (!running) {
            Collects.forEach(topicMap, new Consumer2<String, MessageTopic>() {
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
        Collects.forEach(topicMap, new Consumer2<String, MessageTopic>() {
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

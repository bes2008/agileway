package com.jn.agileway.dmmq.core;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

/**
 * Just send message to the 'DEFAULT' topic if the topicName is not specified
 *
 * @param <M>
 */
public class SimpleProducer<M> implements Producer<M> {
    private static final Logger logger = Loggers.getLogger(SimpleProducer.class);
    private TopicAllocator<M> topicAllocator;
    private MessageTopicDispatcher dispatcher;

    public TopicAllocator<M> getTopicAllocator() {
        return topicAllocator;
    }

    public void setTopicAllocator(TopicAllocator<M> topicAllocator) {
        this.topicAllocator = topicAllocator;
    }

    @Override
    public void publish(M message) {
        publish(null, message);
    }

    @Override
    public void publish(@Nullable String topicName, @NonNull M message) {
        Preconditions.checkNotNull(message);
        topicName = getTopic(topicName, message);
        if (Strings.isEmpty(topicName)) {
            logger.warn("not found an invalid topic for the message: {}", message);
            return;
        }
        dispatcher.publish(topicName, message);
    }

    private String getTopic(String topicName, M message) {
        if (Strings.isEmpty(topicName)) {
            return topicAllocator.apply(message);
        }
        return topicName;
    }

    @Override
    public void setMessageTopicDispatcher(MessageTopicDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public MessageTopicDispatcher getMessageTopicDispatcher() {
        return dispatcher;
    }


}

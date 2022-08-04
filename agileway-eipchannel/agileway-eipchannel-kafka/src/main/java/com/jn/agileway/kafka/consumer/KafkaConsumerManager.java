package com.jn.agileway.kafka.consumer;

import com.jn.langx.registry.GenericRegistry;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.util.List;

/**
 * <pre>
 * 1. 支持为一个consumer 分配多个topic，允许一个topic 被多个 consumer group 消费。
 * 2. 支持为topic 分组，一个topic 只能属于一个topic 组。
 *    一个 topic group 下的 相同cluster下的 topics 只能被一个consumer 消费， 一个 consumer 可以消费多个 topic group
 * </pre>
 */
public class KafkaConsumerManager extends GenericRegistry<KafkaConsumerRef> implements TopicSubscribeEventListener {
    private final static Logger logger = Loggers.getLogger(KafkaConsumerManager.class);
    private ConsumerIdSupplier consumerIdSupplier = new ConsumerIdSupplier() {
        @Override
        public String get(String s) {
            return s;
        }
    };

    public void setConsumerIdSupplier(ConsumerIdSupplier consumerIdSupplier) {
        this.consumerIdSupplier = consumerIdSupplier;
    }

    @Override
    public void on(TopicSubscribeEvent event) {
        if (event == null) {
            return;
        }
        TopicSubscribeEventType eventType = event.getType();
        Preconditions.checkNotNull(eventType, "topic subscribe event invalid: {}", event);

        String consumerIdentify = event.getSource();
        Preconditions.checkNotEmpty(consumerIdentify, "topic subscribe event invalid: {}", event);

        String consumerId = consumerIdSupplier.get(consumerIdentify);
        if (Strings.isBlank(consumerId) || !contains(consumerId)) {
            logger.warn("unknown consumer: {}", consumerIdentify);
            return;
        }

        List<String> topics = event.getTopics();
        if (topics == null) {
            if (eventType != TopicSubscribeEventType.REPLACE) {
                return;
            }
            topics = Collects.emptyArrayList();
        }

        KafkaConsumerRef consumerRef = get(consumerId);
        TopicConsumerContext subscribeContext = consumerRef.getSubscribeContext();
        switch (eventType) {
            case ADD:
                subscribeContext.addTopics(topics);
                break;
            case DELETE:
                subscribeContext.removeTopics(topics);
                break;
            case REPLACE:
                subscribeContext.replaceTopics(topics);
                break;
            default:
                break;
        }

    }
}

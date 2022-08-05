package com.jn.agileway.eipchannel.topic.consumer;

import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.List;

/**
 * <pre>
 * 1. 支持为一个consumer 分配多个topic，允许一个topic 被多个 consumer group 消费。
 * 2. 支持为topic 分组，一个topic 只能属于一个topic 组。
 *    一个 topic group 下的 相同cluster下的 topics 只能被一个consumer 消费， 一个 consumer 可以消费一个cluster下的多个 topic group
 *
 * example:
 *                  /   topic-group-1@cluster-1       : topic-1-1, topic-1-2, topic-1-3
 *     consumer-1   -   topic-group-2@cluster-1       : topic-2-1, topic-2-2
 *                  \   topic-group-3@cluster-1       : topic-3-1
 *
 *                  /   topic-group-4@cluster-2       : topic-4-1, topic-4-2, topic-4-3
 *     consumer-2   -   topic-group-5@cluster-2       : topic-5-1
 *                  \   topic-group-6@cluster-2       : topic-6-1, topic-6-2
 *
 *                  /   topic-group-4@cluster-1       : topic-4-1, topic-4-2, topic-4-3
 *     consumer-3   -   topic-group-5@cluster-1       : topic-5-1
 *                  \   topic-group-6@cluster-1       : topic-6-1, topic-6-2
 *
 * </pre>
 */
public class MQConsumerManager implements TopicSubscribeEventListener {
    private final static Logger logger = Loggers.getLogger(MQConsumerManager.class);
    /**
     * key: cluster
     * value： maintainer1, maintainer2
     */
    private MultiValueMap<String, GroupedTopicsSubscriptionMaintainer> clusterConsumersRegistry;

    @Override
    public void on(TopicSubscribeEvent event) {
        if (event == null) {
            return;
        }
        final TopicSubscribeEventType eventType = event.getType();
        Preconditions.checkNotNull(eventType, "topic subscribe event invalid: {}", event);

        final String topicGroup = event.getSource();
        Preconditions.checkNotEmpty(topicGroup, "invalid topic group : {}", event);
        List<TopicSubscribeEvent.TopicURLComponents> clusterTopicsList = event.getTopicComponents();
        if (clusterTopicsList == null) {
            if (eventType != TopicSubscribeEventType.REPLACE) {
                return;
            }
            clusterTopicsList = Collects.emptyArrayList();
        }


        Pipeline.of(clusterTopicsList, new Consumer<TopicSubscribeEvent.TopicURLComponents>() {
            @Override
            public void accept(TopicSubscribeEvent.TopicURLComponents topicURLComponents) {
                final List<String> topics = topicURLComponents.getTopics();
                Collection<GroupedTopicsSubscriptionMaintainer> maintainers = clusterConsumersRegistry.get(topicURLComponents.getCluster());

                Pipeline.of(maintainers)
                        .forEach(new Predicate<GroupedTopicsSubscriptionMaintainer>() {
                            @Override
                            public boolean test(GroupedTopicsSubscriptionMaintainer maintainer) {
                                return maintainer.actualContainsGroup(topicGroup);
                            }
                        }, new Consumer<GroupedTopicsSubscriptionMaintainer>() {
                            @Override
                            public void accept(GroupedTopicsSubscriptionMaintainer maintainer) {
                                switch (eventType) {
                                    case ADD:
                                        if (Objs.isNotEmpty(topics)) {
                                            maintainer.addTopics(topicGroup, topics);
                                        }
                                        break;
                                    case DELETE:
                                        if (Objs.isNotEmpty(topics)) {
                                            maintainer.removeTopics(topicGroup, topics);
                                        }
                                        break;
                                    case REPLACE:
                                        maintainer.replaceTopics(topicGroup, topics);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });

            }
        });

    }
}

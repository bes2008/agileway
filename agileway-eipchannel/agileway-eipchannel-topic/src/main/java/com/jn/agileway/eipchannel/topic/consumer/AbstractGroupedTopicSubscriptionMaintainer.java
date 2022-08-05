package com.jn.agileway.eipchannel.topic.consumer;

import com.jn.langx.AbstractNameable;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.multivalue.CommonMultiValueMap;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.function.Predicate;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractGroupedTopicSubscriptionMaintainer<GroupedTopicsConsumer> extends AbstractNameable implements GroupedTopicsSubscriptionMaintainer<GroupedTopicsConsumer> {

    /**
     * 关联的 consumer
     */
    private GroupedTopicsConsumer consumer;


    /**
     * key: topic group
     * value: topics
     */
    private final MultiValueMap<String, String> expectedTopics = new CommonMultiValueMap<String, String>();

    /**
     * expectedTopics 最后一次修改的时间, mills
     */
    private long expectedTopicsLastModified = 0;

    /**
     * 最后一次 修改订阅情况的时间
     */
    private long lastSubscribeTime;

    /**
     * key: topic
     * value: group
     */
    private Map<String, String> history = Collects.emptyHashMap();

    @Override
    public void setLastSubscribeTime(long lastSubscribeTime) {

    }

    @Override
    public long getLastSubscribeTime() {
        return 0;
    }

    public long getExpectedTopicsLastModified() {
        return expectedTopicsLastModified;
    }

    @Override
    public int getExpectedTopicCount() {
        return Objs.length(getExpectedTopics());
    }

    @Override
    public int getActualTopicCount() {
        return Objs.length(getActualTopics());
    }

    @Override
    public boolean actualContainsGroup(final String group) {
        return Pipeline.of(getActualTopics())
                .anyMatch(new Predicate<String>() {
                    @Override
                    public boolean test(String topic) {
                        return Objs.equals( history.get(topic), group);
                    }
                });
    }

    public void removeTopics(String group, Collection<String> topics) {
        Pipeline.of(this.expectedTopics.get(group)).removeAll(Pipeline.of(topics).clearNulls().asList());
    }

    public void addTopics(String group, Collection<String> topics) {
        expectedTopics.addAll(group, Pipeline.of(topics).clearNulls().asList());
    }

    public void replaceTopics(String group, Collection<String> topics) {
        this.expectedTopics.clear();
        this.expectedTopics.addAll(group, topics);
    }


}

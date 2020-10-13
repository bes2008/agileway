package com.jn.agileway.dmmq.core;

public interface TopicAllocatorAware<M> {

    void setTopicAllocator(TopicAllocator<M> topicAllocator);

    TopicAllocator<M> getTopicAllocator();
}

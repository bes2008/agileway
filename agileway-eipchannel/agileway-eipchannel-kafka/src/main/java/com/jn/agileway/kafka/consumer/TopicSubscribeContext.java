package com.jn.agileway.kafka.consumer;

import com.jn.langx.util.collection.Pipeline;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TopicSubscribeContext {
    private ReentrantReadWriteLock topicLock = new ReentrantReadWriteLock();
    private final Set<String> expectedTopics = new HashSet<String>();
    private long expectedTopicsLastModified = 0;


    public void removeTopics(Collection<String> topics) {
        topicLock.writeLock().lock();
        this.expectedTopics.removeAll(Pipeline.of(topics).clearNulls().asList());
        topicLock.writeLock().unlock();
    }

    public void addTopics(Collection<String> topics) {
        topicLock.writeLock().lock();
        Pipeline.of(topics).clearNulls().addTo(this.expectedTopics);
        topicLock.writeLock().lock();
    }

    public void replaceTopics(Collection<String> topics){
        topicLock.writeLock().lock();
        this.expectedTopics.clear();
        Pipeline.of(topics).clearNulls().addTo(this.expectedTopics);
        topicLock.writeLock().lock();
    }

    public long getExpectedTopicsLastModified() {
        return expectedTopicsLastModified;
    }
}

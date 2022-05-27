package com.jn.agileway.kafka.consumer;


import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TopicConsumerContext {
    private ReentrantReadWriteLock topicLock = new ReentrantReadWriteLock();
    /**
     * key: topic group
     * value: topics
     */
    private final Map<String, String> expectedTopics = new HashMap<String, String>();
    private long expectedTopicsLastModified = 0;


    public void removeTopics(Collection<String> topics) {
        topicLock.writeLock().lock();
       // this.expectedTopics.removeAll(Pipeline.of(topics).clearNulls().asList());
        topicLock.writeLock().unlock();
    }

    public void addTopics(Collection<String> topics) {
        topicLock.writeLock().lock();
       // Pipeline.of(topics).clearNulls().addTo(this.expectedTopics);
        topicLock.writeLock().lock();
    }

    public void replaceTopics(Collection<String> topics) {
        topicLock.writeLock().lock();
        this.expectedTopics.clear();
        //Pipeline.of(topics).clearNulls().addTo(this.expectedTopics);
        topicLock.writeLock().lock();
    }

    public long getExpectedTopicsLastModified() {
        return expectedTopicsLastModified;
    }
}

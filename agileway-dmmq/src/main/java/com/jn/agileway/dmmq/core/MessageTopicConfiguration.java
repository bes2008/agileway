package com.jn.agileway.dmmq.core;

import com.jn.agileway.dmmq.core.allocator.DefaultTopicAllocator;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executor;

public class MessageTopicConfiguration {
    @NonNull
    private String name = DefaultTopicAllocator.TOPIC_DEFAULT;

    @NonNull
    private int ringBufferSize = 8192; // power(2)
    @NonNull
    private Executor executor;
    @NonNull
    private ProducerType producerType = ProducerType.MULTI;
    @Nullable
    private WaitStrategy waitStrategy;

    @NonNull
    private MessageTranslator messageTranslator = new DefaultMessageTranslator();

    public int getRingBufferSize() {
        return ringBufferSize;
    }

    public void setRingBufferSize(int ringBufferSize) {
        this.ringBufferSize = ringBufferSize;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public ProducerType getProducerType() {
        return producerType;
    }

    public void setProducerType(ProducerType producerType) {
        this.producerType = producerType;
    }

    public WaitStrategy getWaitStrategy() {
        return waitStrategy;
    }

    public void setWaitStrategy(WaitStrategy waitStrategy) {
        this.waitStrategy = waitStrategy;
    }


    @Deprecated
    public void setMessageTranslatorClass(String messageTranslatorClass) {
    }

    public MessageTranslator getMessageTranslator() {
        return messageTranslator;
    }

    public void setMessageTranslator(MessageTranslator messageTranslator) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.jn.agileway.dmmq.disruptor;

import com.jn.agileway.dmmq.core.MessageTopicConfiguration;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.reflect.Reflects;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

public class DisruptorTopicConfiguration extends MessageTopicConfiguration {
    @NonNull
    private int ringBufferSize = 8192; // power(2)
    @NonNull
    private ProducerType producerType = ProducerType.MULTI;
    @Nullable
    private WaitStrategy waitStrategy;

    private String messageTranslatorClass = Reflects.getFQNClassName(DefaultMessageTranslator.class);
    @NonNull
    private DisruptorMessageTranslator messageTranslator = new DefaultMessageTranslator();


    public int getRingBufferSize() {
        return ringBufferSize;
    }

    public void setRingBufferSize(int ringBufferSize) {
        this.ringBufferSize = ringBufferSize;
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
    public String getMessageTranslatorClass() {
        return messageTranslatorClass;
    }

    public void setMessageTranslatorClass(String messageTranslatorClass) {
        this.messageTranslatorClass = messageTranslatorClass;
    }

    public DisruptorMessageTranslator getMessageTranslator() {
        return messageTranslator;
    }

    public void setMessageTranslator(DisruptorMessageTranslator messageTranslator) {
        this.messageTranslator = messageTranslator;
    }

}

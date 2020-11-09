package com.jn.agileway.dmmq.core;

import com.lmax.disruptor.EventTranslator;

/**
 * 多个topic不能共用同一个translator
 *
 * @param <M>
 */
public interface MessageTranslator<M> extends EventTranslator<MessageHolder<M>>, TopicNameAware {
    void setMessage(M message);

    M getMessage();

    @Override
    void translateTo(MessageHolder<M> event, long sequence);
}

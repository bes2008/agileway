package com.jn.agileway.dmmq.disruptor;

import com.jn.agileway.dmmq.core.MessageHolder;
import com.jn.agileway.dmmq.core.TopicNameAware;
import com.lmax.disruptor.EventTranslator;

/**
 * 多个topic不能共用同一个translator
 *
 * @param <M>
 */
public interface DisruptorMessageTranslator<M> extends EventTranslator<MessageHolder<M>>, TopicNameAware {
    void setMessage(M message);

    M getMessage();

    @Override
    void translateTo(MessageHolder<M> event, long sequence);
}

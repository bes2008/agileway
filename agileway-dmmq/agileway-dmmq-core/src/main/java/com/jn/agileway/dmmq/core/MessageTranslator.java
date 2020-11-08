package com.jn.agileway.dmmq.core;


/**
 * 多个topic不能共用同一个translator
 *
 * @param <M>
 */
public interface MessageTranslator<M> extends TopicNameAware {
    void setMessage(M message);

    M getMessage();

    void translateTo(MessageHolder<M> event, long sequence);
}


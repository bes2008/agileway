package com.jn.agileway.eipchannel.core.transformer;

import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.Transformer;

public interface MessageTransformer extends Transformer<Message<?>, Message<?>> {
    Message<?> transform(Message<?> message);
}
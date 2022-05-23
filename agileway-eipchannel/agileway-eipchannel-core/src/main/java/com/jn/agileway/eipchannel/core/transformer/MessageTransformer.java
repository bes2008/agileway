package com.jn.agileway.eipchannel.core.transformer;

import com.jn.agileway.eipchannel.core.message.Message;

public interface MessageTransformer {
    Message<?> transform(Message<?> message);
}
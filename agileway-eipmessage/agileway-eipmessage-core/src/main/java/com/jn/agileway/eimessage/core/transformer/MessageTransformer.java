package com.jn.agileway.eimessage.core.transformer;

import com.jn.agileway.eimessage.core.message.Message;

public interface MessageTransformer {
    Message<?> transform(Message<?> message);
}
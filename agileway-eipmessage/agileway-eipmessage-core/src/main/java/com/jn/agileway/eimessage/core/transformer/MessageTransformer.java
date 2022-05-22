package com.jn.agileway.eimessage.core.transformer;

import com.jn.agileway.eimessage.core.Message;

public interface MessageTransformer {
    Message<?> transform(Message<?> message);
}
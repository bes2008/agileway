package com.jn.agileway.eimessage.core.endpoint.pollingsource;

import com.jn.agileway.eimessage.core.message.Message;

public interface InboundMessageSource {
    Message<?> poll(long timeoutInMills);
}
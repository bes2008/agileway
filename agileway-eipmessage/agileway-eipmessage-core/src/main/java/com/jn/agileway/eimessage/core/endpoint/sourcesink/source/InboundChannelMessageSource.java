package com.jn.agileway.eimessage.core.endpoint.sourcesink.source;

import com.jn.agileway.eimessage.core.message.Message;

public interface InboundChannelMessageSource {
    Message<?> poll(long timeoutInMills);
}

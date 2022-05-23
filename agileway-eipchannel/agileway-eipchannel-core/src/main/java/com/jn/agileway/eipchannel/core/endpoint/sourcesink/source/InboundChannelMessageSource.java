package com.jn.agileway.eipchannel.core.endpoint.sourcesink.source;

import com.jn.agileway.eipchannel.core.message.Message;

public interface InboundChannelMessageSource {
    Message<?> poll(long timeoutInMills);
}

package com.jn.agileway.eipchannel.core.endpoint.sourcesink.source;

import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.lifecycle.Lifecycle;

public interface InboundChannelMessageSource extends Lifecycle {
    Message<?> poll(long timeoutInMills);
}

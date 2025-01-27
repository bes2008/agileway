package com.jn.agileway.eipchannel.core.endpoint.exchange;

import com.jn.agileway.eipchannel.core.endpoint.Endpoint;
import com.jn.agileway.eipchannel.core.message.Message;

public interface RequestReplyExchanger extends Endpoint {
    Message<?> exchange(Message<?> request);
}

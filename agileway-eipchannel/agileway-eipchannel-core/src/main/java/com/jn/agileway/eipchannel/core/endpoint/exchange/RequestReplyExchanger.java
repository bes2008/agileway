package com.jn.agileway.eipchannel.core.endpoint.exchange;

import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.agileway.eipchannel.core.endpoint.Endpoint;

public interface RequestReplyExchanger extends Endpoint {
    Message<?> exchange(Message<?> request);
}

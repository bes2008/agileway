package com.jn.agileway.eimessage.core.endpoint.exchange;

import com.jn.agileway.eimessage.core.message.Message;
import com.jn.agileway.eimessage.core.endpoint.Endpoint;

public interface RequestReplyExchanger extends Endpoint {
    Message<?> exchange(Message<?> request);
}

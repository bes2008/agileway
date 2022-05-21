package com.jn.agileway.eimessage.core.endpoint.exchanger;

import com.jn.agileway.eimessage.core.Message;
import com.jn.agileway.eimessage.core.endpoint.Endpoint;

public interface RequestReplyExchanger extends Endpoint {
    Message<?> exchange(Message<?> request);
}

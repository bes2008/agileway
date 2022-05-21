package com.jn.agileway.eimessage.core.endpoint.exchanger;

import com.jn.agileway.eimessage.core.Message;

public interface RequestReplyExchanger {
    Message<?> exchange(Message<?> request);
}

package com.jn.agileway.eimessage.core.endpoints.consumer;

import com.jn.agileway.eimessage.core.Message;

public interface MessageDispatcher {

    boolean addHandler(MessageHandler handler);

    boolean removeHandler(MessageHandler handler);

    boolean dispatch(Message<?> message);

}


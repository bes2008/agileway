package com.jn.agileway.eimessage.core.endpoint.pubsub.consumer;

import com.jn.agileway.eimessage.core.model.Message;
import com.jn.agileway.eimessage.core.handler.MessageHandler;

public interface MessageDispatcher {

    boolean addHandler(MessageHandler handler);

    boolean removeHandler(MessageHandler handler);

    boolean dispatch(Message<?> message);

}


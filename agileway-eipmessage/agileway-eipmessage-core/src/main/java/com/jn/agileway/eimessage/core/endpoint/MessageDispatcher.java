package com.jn.agileway.eimessage.core.endpoint;

import com.jn.agileway.eimessage.core.Message;
import com.jn.agileway.eimessage.core.handler.MessageHandler;

public interface MessageDispatcher {

    boolean addHandler(MessageHandler handler);

    boolean removeHandler(MessageHandler handler);

    boolean dispatch(Message<?> message);

}


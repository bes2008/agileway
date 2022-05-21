package com.jn.agileway.eimessage.core.endpoint;

import com.jn.agileway.eimessage.core.handler.MessageHandler;

/**
 * 通常在 consumer 上使用
 */
public interface MessageDispatcher extends MessageHandler {

    boolean addHandler(MessageHandler handler);

    boolean removeHandler(MessageHandler handler);

}


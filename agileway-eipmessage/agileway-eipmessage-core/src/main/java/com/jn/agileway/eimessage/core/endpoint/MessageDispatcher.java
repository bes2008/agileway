package com.jn.agileway.eimessage.core.endpoint;

import com.jn.agileway.eimessage.core.message.MessageHandler;

/**
 * 通常在 consumer 上使用
 */
public interface MessageDispatcher extends MessageHandler {

    /**
     * 在 consumer中使用时， 该过程代表了 订阅
     *
     * @param handler
     * @return
     */
    boolean addHandler(MessageHandler handler);

    boolean removeHandler(MessageHandler handler);

}


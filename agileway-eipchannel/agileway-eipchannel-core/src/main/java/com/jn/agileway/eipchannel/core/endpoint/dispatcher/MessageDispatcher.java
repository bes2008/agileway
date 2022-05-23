package com.jn.agileway.eipchannel.core.endpoint.dispatcher;

import com.jn.agileway.eipchannel.core.endpoint.Endpoint;
import com.jn.agileway.eipchannel.core.message.Message;

/**
 * 通常在 consumer 上使用
 */
public interface MessageDispatcher extends Endpoint {

    /**
     * 在 consumer中使用时， 该过程代表了 订阅
     */
    boolean addHandler(MessageHandler handler);

    boolean removeHandler(MessageHandler handler);

    boolean dispatch(Message<?> message);
}


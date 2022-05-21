package com.jn.agileway.eimessage.core.endpoint.pubsub;

import com.jn.agileway.eimessage.core.endpoint.Endpoint;
import com.jn.agileway.eimessage.core.endpoint.mapper.MessageMapper;

public interface PubSubEndpoint extends Endpoint {
    MessageFilterChain getFilterChain();

    void setFilterChain(MessageFilterChain chain);

    void setMessageMapper(MessageMapper messageMapper);

    MessageMapper getMessageMapper();
}

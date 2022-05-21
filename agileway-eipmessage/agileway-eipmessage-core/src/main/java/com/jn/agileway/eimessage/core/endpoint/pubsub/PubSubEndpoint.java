package com.jn.agileway.eimessage.core.endpoint.pubsub;

import com.jn.agileway.eimessage.core.endpoint.Endpoint;
import com.jn.agileway.eimessage.core.endpoint.mapper.MessageMapper;

public interface PubSubEndpoint<T> extends Endpoint {

    void setMessageMapper(MessageMapper<T>  messageMapper);

    MessageMapper<T>  getMessageMapper();
}

package com.jn.agileway.eipchannel.core.endpoint.pubsub;

import com.jn.agileway.eipchannel.core.endpoint.Endpoint;
import com.jn.agileway.eipchannel.core.endpoint.mapper.MessageMapper;

public interface PubSubEndpoint<T> extends Endpoint {

    void setMessageMapper(MessageMapper<T> messageMapper);

    MessageMapper<T> getMessageMapper();
}

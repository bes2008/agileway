package com.jn.agileway.eimessage.core.endpoint.pubsub;

import com.jn.agileway.eimessage.core.endpoint.Endpoint;
import com.jn.agileway.eimessage.core.endpoint.mapper.MessageMapper;

public interface MessageProducer extends Endpoint {
    void setMessageMapper(MessageMapper messageMapper);
    MessageMapper getMessageMapper();


}

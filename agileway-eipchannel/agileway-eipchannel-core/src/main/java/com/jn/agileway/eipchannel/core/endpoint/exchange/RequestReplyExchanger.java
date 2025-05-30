package com.jn.agileway.eipchannel.core.endpoint.exchange;

import com.jn.agileway.eipchannel.core.endpoint.Endpoint;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.util.concurrent.promise.Promise;

public interface RequestReplyExchanger<REQ extends Message<?>, RESP extends Message<?>> extends Endpoint {
    Promise<RESP> exchangeAsync(REQ request);

    RESP exchange(REQ request);
}

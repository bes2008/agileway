package com.jn.agileway.eipchannel.core.endpoint.mapper;

import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.util.bean.ReversibleModeMapper;

public interface MessageMapper<T> extends ReversibleModeMapper<T, Message<?>> {
    @Override
    Message<?> map(T t);

    @Override
    T reverseMap(Message<?> message);
}

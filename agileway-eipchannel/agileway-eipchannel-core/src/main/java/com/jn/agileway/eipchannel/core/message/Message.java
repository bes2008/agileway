package com.jn.agileway.eipchannel.core.message;

public interface Message<T> {
    T getPayload();

    MessageHeaders getHeaders();
}

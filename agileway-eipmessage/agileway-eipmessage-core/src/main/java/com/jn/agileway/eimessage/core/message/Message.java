package com.jn.agileway.eimessage.core.message;

public interface Message<T> {
    T getPayload();
    MessageHeaders getHeaders();
}

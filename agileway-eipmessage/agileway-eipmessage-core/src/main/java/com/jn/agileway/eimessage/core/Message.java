package com.jn.agileway.eimessage.core;

public interface Message<T> {
    T getPayload();
    MessageHeaders getHeaders();
}

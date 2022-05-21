package com.jn.agileway.eimessage.core.model;

public interface Message<T> {
    T getPayload();
    MessageHeaders getHeaders();
}

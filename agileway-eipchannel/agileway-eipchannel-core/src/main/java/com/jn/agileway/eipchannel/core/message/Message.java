package com.jn.agileway.eipchannel.core.message;

import java.io.Serializable;
import java.util.Map;

public interface Message<T> extends Serializable {
    T getPayload();

    MessageHeaders getHeaders();
}
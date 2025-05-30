package com.jn.agileway.eipchannel.core.message;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;

import java.util.Map;

public class BaseMessage<H, T> implements Message<T> {

    @NonNull
    protected T payload;

    @NonNull
    protected MessageHeaders<H> headers;

    @Override
    public T getPayload() {
        return payload;
    }

    @Override
    public MessageHeaders<H> getHeaders() {
        return headers;
    }

    public int hashCode() {
        return this.headers.hashCode() * 23 + Objs.hashCode(this.payload);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (obj instanceof GenericMessage<?>) {
                GenericMessage<?> other = (GenericMessage<?>) obj;
                if (!this.headers.getId().equals(other.headers.getId())) {
                    return false;
                }
                return this.headers.equals(other.headers) && this.payload.equals(other.payload);
            }
        }
        return false;
    }

}

package com.jn.agileway.httpclient.soap;

public class SoapBody<T> {
    private T payload;

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}

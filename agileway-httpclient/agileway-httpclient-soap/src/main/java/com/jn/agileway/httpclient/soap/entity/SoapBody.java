package com.jn.agileway.httpclient.soap.entity;

public class SoapBody<T> {
    private T payload;

    public SoapBody() {
    }

    public SoapBody(T payload) {
        this.payload = payload;
    }
    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}

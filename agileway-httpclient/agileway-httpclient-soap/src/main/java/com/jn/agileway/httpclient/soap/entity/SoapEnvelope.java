package com.jn.agileway.httpclient.soap.entity;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;

public class SoapEnvelope {
    @Nullable
    private SoapHeader header;
    @NonNull
    private SoapBody body;

    public SoapHeader getHeader() {
        return header;
    }

    public void setHeader(SoapHeader header) {
        this.header = header;
    }

    public SoapBody getBody() {
        return body;
    }

    public void setBody(SoapBody body) {
        this.body = body;
    }
}

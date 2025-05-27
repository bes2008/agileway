package com.jn.agileway.httpclient.soap.entity;

import com.jn.agileway.httpclient.soap.utils.SoapVersion;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;

/**
 * https://www.w3.org/2003/05/soap-envelope/
 */
public class SoapEnvelope {
    @Nullable
    private SoapHeader header;
    @NonNull
    private SoapBody body;

    private SoapVersion version;

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

    public SoapVersion getVersion() {
        return version;
    }

    public void setVersion(SoapVersion version) {
        this.version = version;
    }
}

package com.jn.agileway.httpclient.soap.entity;

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

    private final SoapMessageMetadata metadata = new SoapMessageMetadata();
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

    public SoapMessageMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(SoapMessageMetadata metadata) {
        if (metadata != null) {
            this.metadata.setBinding(metadata.getBinding());
            this.metadata.setNamespacePrefix(metadata.getNamespacePrefix());
        }
    }
}

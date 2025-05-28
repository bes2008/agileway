package com.jn.agileway.httpclient.soap.entity;

import java.util.List;

public class SoapMessage {
    private SoapEnvelope envelope;
    private List<SoapAttachment> attachments;
    private SoapMessageMetadata metadata;

    public SoapEnvelope getEnvelope() {
        return envelope;
    }

    public void setEnvelope(SoapEnvelope envelope) {
        this.envelope = envelope;
    }

    public List<SoapAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<SoapAttachment> attachments) {
        this.attachments = attachments;
    }

    public SoapMessageMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(SoapMessageMetadata metadata) {
        this.metadata = metadata;
    }
}

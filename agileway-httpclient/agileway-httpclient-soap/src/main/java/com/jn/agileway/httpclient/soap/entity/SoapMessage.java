package com.jn.agileway.httpclient.soap.entity;

import java.util.List;

public class SoapMessage {
    private SoapEnvelope envelope;
    private List<SoapAttachment> attachments;
    private final SoapMessageMetadata metadata = new SoapMessageMetadata();

    public SoapMessage() {
    }

    public SoapMessage(SoapMessageMetadata metadata) {
        setMetadata(metadata);
    }

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
        if (metadata != null) {
            this.metadata.setBinding(metadata.getBinding());
            this.metadata.setNamespacePrefix(metadata.getNamespacePrefix());
        }
    }
}

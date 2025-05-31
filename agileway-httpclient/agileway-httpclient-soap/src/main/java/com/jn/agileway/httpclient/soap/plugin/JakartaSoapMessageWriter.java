package com.jn.agileway.httpclient.soap.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import jakarta.xml.soap.SOAPMessage;

import java.io.ByteArrayOutputStream;

public class JakartaSoapMessageWriter implements HttpRequestPayloadWriter {
    @Override
    public boolean canWrite(HttpRequest<?> request) {
        if (request.getPayload() instanceof SOAPMessage) {
            return true;
        }
        return false;
    }

    @Override
    public void write(HttpRequest<?> request, ByteArrayOutputStream output) throws Exception {
        SOAPMessage soapMessage = (SOAPMessage) request.getPayload();
        soapMessage.writeTo(output);
    }
}

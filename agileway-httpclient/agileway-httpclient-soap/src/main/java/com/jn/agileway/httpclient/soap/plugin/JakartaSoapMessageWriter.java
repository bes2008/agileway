package com.jn.agileway.httpclient.soap.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.content.HttpRequestContentWriter;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequest;
import jakarta.xml.soap.SOAPMessage;

public class JakartaSoapMessageWriter implements HttpRequestContentWriter {
    @Override
    public boolean canWrite(HttpRequest request) {
        if (request.getPayload() instanceof SOAPMessage) {
            return true;
        }
        return false;
    }

    @Override
    public void write(HttpRequest request, UnderlyingHttpRequest output) throws Exception {
        SOAPMessage soapMessage = (SOAPMessage) request.getPayload();
        soapMessage.writeTo(output.getPayload());
    }
}

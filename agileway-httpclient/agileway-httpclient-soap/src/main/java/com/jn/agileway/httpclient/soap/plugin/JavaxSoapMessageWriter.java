package com.jn.agileway.httpclient.soap.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.content.HttpRequestContentWriter;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequest;

import javax.xml.soap.SOAPMessage;

public class JavaxSoapMessageWriter implements HttpRequestContentWriter {
    @Override
    public boolean canWrite(HttpRequest request) {
        if (request.getContent() instanceof SOAPMessage) {
            return true;
        }
        return false;
    }

    @Override
    public void write(HttpRequest request, UnderlyingHttpRequest output) throws Exception {
        SOAPMessage soapMessage = (SOAPMessage) request.getContent();
        soapMessage.writeTo(output.getContent());
    }
}

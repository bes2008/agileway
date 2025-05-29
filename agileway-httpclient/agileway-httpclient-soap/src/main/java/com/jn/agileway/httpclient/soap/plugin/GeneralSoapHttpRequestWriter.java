package com.jn.agileway.httpclient.soap.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.content.HttpRequestContentWriter;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpRequest;
import com.jn.agileway.httpclient.soap.entity.SoapMessage;
import com.jn.agileway.httpclient.soap.utils.SOAPs;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.io.Charsets;

import jakarta.xml.soap.SOAPMessage;
import org.w3c.dom.Element;

import java.io.IOException;

class GeneralSoapHttpRequestWriter implements HttpRequestContentWriter {
    @Override
    public boolean canWrite(HttpRequest request) {
        Object body = request.getContent();
        if (body instanceof SOAPMessage) {
            return false;
        }
        if (body instanceof SoapMessage) {
            return false;
        }
        if (body instanceof Element) {
            return false;
        }
        return true;
    }

    @Override
    public void write(HttpRequest request, UnderlyingHttpRequest output) throws IOException {
        Object body = request.getContent();
        try {


            SoapMessage soapMessage = (SoapMessage) body;
            String soapEnvelopeXml = SOAPs.marshalSoapEnvelope(soapMessage);
            // TODO
            output.getContent().write(soapEnvelopeXml.getBytes(Charsets.UTF_8));
        } catch (Throwable e) {
            throw Throwables.wrapAsRuntimeException(e);
        }
    }
}

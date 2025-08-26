package com.jn.agileway.httpclient.soap.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.payload.HttpRequestPayloadWriter;
import com.jn.agileway.httpclient.soap.entity.SoapMessage;
import com.jn.agileway.httpclient.soap.utils.SOAPs;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.io.Charsets;

import java.io.IOException;
import java.io.OutputStream;

class JavaBeanSoapHttpRequestWriter implements HttpRequestPayloadWriter {
    @Override
    public boolean canWrite(HttpRequest<?> request) {
        return (request.getPayload() instanceof SoapMessage);
    }

    @Override
    public void write(HttpRequest<?> request, OutputStream output) throws IOException {
        Object body = request.getPayload();
        try {
            SoapMessage soapMessage = (SoapMessage) body;
            String soapEnvelopeXml = SOAPs.marshalSoapEnvelope(soapMessage);
            output.write(soapEnvelopeXml.getBytes(Charsets.UTF_8));
        } catch (Throwable e) {
            throw Throwables.wrapAsRuntimeException(e);
        }
    }
}

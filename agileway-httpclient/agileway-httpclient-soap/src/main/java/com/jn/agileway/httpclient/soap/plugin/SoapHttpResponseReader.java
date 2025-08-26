package com.jn.agileway.httpclient.soap.plugin;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.payload.HttpResponsePayloadReader;
import com.jn.agileway.httpclient.soap.utils.SOAPs;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.mime.MediaType;

import java.lang.reflect.Type;

public class SoapHttpResponseReader implements HttpResponsePayloadReader<Object> {
    @Override
    public boolean canRead(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) {
        return response.getStatusCode() == 200;
    }

    @Override
    public Object read(HttpResponse<byte[]> response, MediaType contentType, Type expectedContentType) throws Exception {
        String soapEnvelopeXml = new String(response.getPayload(), Charsets.UTF_8);
        return SOAPs.unmarshalSoapPayload(soapEnvelopeXml, (Class) expectedContentType);
    }
}

package com.jn.agileway.httpclient.soap.plugin;

import com.jn.agileway.httpclient.core.content.HttpResponsePayloadReader;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.soap.utils.SOAPs;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.mime.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;

public class SoapHttpResponseReader implements HttpResponsePayloadReader {
    @Override
    public boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) {
        return response.getStatusCode() == 200;
    }

    @Override
    public Object read(UnderlyingHttpResponse underlyingHttpResponse, MediaType contentType, Type expectedContentType) throws Exception {
        InputStream inputStream = underlyingHttpResponse.getPayload();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOs.copy(inputStream, baos);
        String soapEnvelopeXml = baos.toString(Charsets.UTF_8.name());
        return SOAPs.unmarshalSoapPayload(soapEnvelopeXml, (Class) expectedContentType);
    }
}

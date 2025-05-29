package com.jn.agileway.httpclient.soap.plugin;

import com.jn.agileway.httpclient.core.content.HttpResponseContentReader;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.soap.utils.SoapFaults;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.mime.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;

public class SoapFaultResponseReader implements HttpResponseContentReader {
    @Override
    public boolean canRead(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) {
        return response.getStatusCode() >= 400;
    }

    @Override
    public Object read(UnderlyingHttpResponse response, MediaType contentType, Type expectedContentType) throws Exception {
        InputStream inputStream = response.getContent();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOs.copy(inputStream, baos);
        String soapEnvelopeXml = baos.toString(Charsets.UTF_8.name());
        return SoapFaults.unmarshalSoapFault(soapEnvelopeXml);
    }
}

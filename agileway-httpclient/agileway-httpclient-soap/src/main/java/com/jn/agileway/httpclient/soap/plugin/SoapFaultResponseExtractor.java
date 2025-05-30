package com.jn.agileway.httpclient.soap.plugin;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.core.content.HttpResponsePayloadExtractor;
import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.agileway.httpclient.soap.entity.SoapFault;
import com.jn.agileway.httpclient.soap.utils.SoapFaults;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class SoapFaultResponseExtractor implements HttpResponsePayloadExtractor {

    @Override
    public HttpResponse extract(UnderlyingHttpResponse underlyingHttpResponse) throws Exception {
        InputStream inputStream = underlyingHttpResponse.getPayload();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOs.copy(inputStream, baos);
        String soapEnvelopeXml = baos.toString(Charsets.UTF_8.name());
        SoapFault soapFault = SoapFaults.unmarshalSoapFault(soapEnvelopeXml);
        HttpResponse response = new HttpResponse(underlyingHttpResponse, soapFault);
        String message = null;
        if (Strings.isNotBlank(soapFault.getSubCode())) {
            message = StringTemplates.formatWithPlaceholder("soap error: Code: {}, SubCode: {}, Reason: {}", soapFault.getCode(), soapFault.getSubCode(), soapFault.getReason());
        } else {
            message = StringTemplates.formatWithPlaceholder("soap error: Code: {}, Reason: {}", soapFault.getCode(), soapFault.getReason());
        }
        response.setErrorMessage(message);
        return response;
    }
}

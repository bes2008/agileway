package com.jn.agileway.httpclient.soap.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.interceptor.HttpRequestInterceptor;
import com.jn.agileway.httpclient.soap.entity.*;
import com.jn.agileway.httpclient.soap.exception.MalformedSoapMessageException;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;

import jakarta.xml.soap.SOAPMessage;

class SoapHttpRequestInterceptor implements HttpRequestInterceptor {
    @Override
    public void intercept(HttpRequest request) {
        if (request.getMethod() != HttpMethod.POST) {
            return;
        }
        Object httpContent = request.getContent();
        if (httpContent instanceof SOAPMessage) {
            return;
        }
        if (httpContent instanceof SoapMessage) {
            return;
        }
        toSoapMessage(request.getHeaders(), httpContent);
    }

    private SoapMessage toSoapMessage(HttpHeaders headers, Object content) {
        MediaType contentType = headers.getContentType();
        SoapBinding binding = null;
        if (MediaType.APPLICATION_SOAP12_XML.equalsTypeAndSubtype(contentType)) {
            binding = SoapBinding.SOAP12_HTTP;
        }
        if (MediaType.APPLICATION_ATOM_XML.equalsTypeAndSubtype(contentType)) {
            //TODO
            binding = null;
        }
        if (MediaType.TEXT_XML.equalsTypeAndSubtype(contentType)) {
            binding = SoapBinding.SOAP11_HTTP;
        }

        String soapNamespacePrefix = headers.getFirst("SOAPNameSpacePrefix");
        SoapMessage soapMessage = new SoapMessage(new SoapMessageMetadata(binding, soapNamespacePrefix));

        if (content instanceof SoapEnvelope) {
            soapMessage.setEnvelope((SoapEnvelope) content);
            return soapMessage;
        }
        if (content instanceof SoapHeader) {
            throw new MalformedSoapMessageException("soap body is required");
        }
        SoapBody soapBody = (content instanceof SoapBody) ? (SoapBody) content : new SoapBody(content);
        soapMessage.setEnvelope(new SoapEnvelope());
        soapMessage.getEnvelope().setBody(soapBody);
        return soapMessage;
    }
}

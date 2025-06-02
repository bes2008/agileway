package com.jn.agileway.httpclient.soap.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.error.exception.BadHttpRequestException;
import com.jn.agileway.httpclient.core.error.exception.UnsupportedHttpMethodException;
import com.jn.agileway.httpclient.core.interceptor.HttpRequestInterceptor;
import com.jn.agileway.httpclient.soap.entity.*;
import com.jn.agileway.httpclient.soap.exception.MalformedSoapMessageException;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;
import com.jn.langx.util.reflect.type.Primitives;
import jakarta.xml.soap.SOAPMessage;
import org.w3c.dom.Node;

class SoapHttpRequestInterceptor implements HttpRequestInterceptor {
    @Override
    public void intercept(HttpRequest request) {
        if (request.getMethod() != HttpMethod.POST) {
            throw new UnsupportedHttpMethodException(request.getMethod(), request.getUri(), "only POST method is supported for SOAP message");
        }
        Object httpContent = request.getPayload();
        if (httpContent == null) {
            throw new BadHttpRequestException(request.getMethod(), request.getUri(), "soap payload is required");
        }
        if (httpContent instanceof SOAPMessage) {
            return;
        }
        if (httpContent instanceof Node) {
            throw new BadHttpRequestException(request.getMethod(), request.getUri(), "unsupported soap payload class: " + httpContent.getClass().getName());
        }
        Class clazz = httpContent.getClass();
        if (Primitives.isPrimitiveOrPrimitiveWrapperType(clazz)) {
            throw new BadHttpRequestException(request.getMethod(), request.getUri(), "unsupported soap payload class: " + httpContent.getClass().getName());
        }

        if (httpContent instanceof javax.xml.soap.SOAPMessage) {
            return;
        }
        if (httpContent instanceof SoapMessage) {
            return;
        }
        SoapMessage soapMessage = toSoapMessage(request.getHttpHeaders(), httpContent);
        request.setPayload(soapMessage);
    }

    private SoapMessage toSoapMessage(HttpHeaders headers, Object content) {
        MediaType contentType = headers.getContentType();
        SoapBinding binding = null;
        if (MediaType.APPLICATION_SOAP12_XML_UTF8.equalsTypeAndSubtype(contentType)) {
            binding = SoapBinding.SOAP12_HTTP;
        }
        if (MediaType.APPLICATION_ATOM_XML.equalsTypeAndSubtype(contentType)) {
            //TODO
            binding = null;
        }
        if (MediaType.TEXT_XML_UTF8.equalsTypeAndSubtype(contentType)) {
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

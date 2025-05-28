package com.jn.agileway.httpclient.soap.plugin;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.interceptor.HttpRequestInterceptor;
import com.jn.agileway.httpclient.soap.entity.SoapBinding;
import com.jn.langx.util.net.http.HttpMethod;
import com.jn.langx.util.net.mime.MediaType;

import javax.xml.soap.SOAPMessage;

public class SoapHttpRequestInterceptor implements HttpRequestInterceptor {
    @Override
    public void intercept(HttpRequest request) {
        if (request.getMethod() != HttpMethod.POST) {
            return;
        }
        Object httpContent = request.getContent();
        if (httpContent instanceof SOAPMessage) {
            return;
        }
        MediaType contentType = request.getHeaders().getContentType();
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
        if (binding == null) {
            return;
        }
        Soap

    }
}

package com.jn.agileway.httpclient.soap.plugin;

import com.jn.agileway.httpclient.core.HttpMessage;
import com.jn.agileway.httpclient.core.plugin.HttpMessageProtocolPlugin;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.net.mime.MediaType;

public class HttpSoapMessagePlugin extends HttpMessageProtocolPlugin {
    @Override
    public boolean availableFor(HttpMessage httpMessage) {
        return isSoapContentType(httpMessage.getHttpHeaders().getContentType());
    }

    private boolean isSoapContentType(MediaType contentType) {
        if (contentType == null) {
            return false;
        }
        if (MediaType.APPLICATION_SOAP12_XML_UTF8.equalsTypeAndSubtype(contentType)) {
            return true;
        }
        if (MediaType.APPLICATION_ATOM_XML.equalsTypeAndSubtype(contentType)) {
            return true;
        }
        if (MediaType.TEXT_XML.equalsTypeAndSubtype(contentType)) {
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "SoapMessagePlugin";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void initInternal() throws InitializationException {
        this.requestInterceptors.add(new SoapHttpRequestInterceptor());

        this.requestPayloadWriters.add(new JavaxSoapMessageWriter());
        this.requestPayloadWriters.add(new JakartaSoapMessageWriter());
        this.requestPayloadWriters.add(new JavaBeanSoapHttpRequestWriter());

        this.responsePayloadReaders.add(new SoapHttpResponseReader());
    }
}

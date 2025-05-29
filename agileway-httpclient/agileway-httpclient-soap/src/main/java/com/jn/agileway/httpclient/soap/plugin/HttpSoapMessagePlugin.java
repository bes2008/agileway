package com.jn.agileway.httpclient.soap.plugin;

import com.jn.agileway.httpclient.core.HttpMessage;
import com.jn.agileway.httpclient.core.plugin.HttpMessageProtocolPlugin;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.net.mime.MediaType;

public class HttpSoapMessagePlugin extends HttpMessageProtocolPlugin {
    @Override
    public boolean availableFor(HttpMessage httpMessage) {
        return isSoapContentType(httpMessage.getHeaders().getContentType());
    }

    private boolean isSoapContentType(MediaType contentType) {
        if (contentType == null) {
            return false;
        }
        if (MediaType.APPLICATION_SOAP12_XML.equalsTypeAndSubtype(contentType)) {
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
    public void init() throws InitializationException {
        this.requestInterceptors.add(new SoapHttpRequestInterceptor());

        this.requestContentWriters.add(new JavaxSoapMessageWriter());
        this.requestContentWriters.add(new JakartaSoapMessageWriter());
        this.requestContentWriters.add(new GeneralSoapHttpRequestWriter());

        this.responseContentReaders.add(new SoapFaultResponseReader());
        this.responseContentReaders.add(new SoapHttpResponseReader());
    }
}

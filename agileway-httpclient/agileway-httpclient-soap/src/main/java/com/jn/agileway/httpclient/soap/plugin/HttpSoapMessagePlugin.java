package com.jn.agileway.httpclient.soap.plugin;

import com.jn.agileway.httpclient.core.HttpMessage;
import com.jn.agileway.httpclient.core.plugin.HttpMessageProtocolPlugin;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.net.mime.MediaType;

public class HttpSoapMessagePlugin extends HttpMessageProtocolPlugin {
    @Override
    public boolean availableFor(HttpMessage httpMessage) {
        MediaType contentType = httpMessage.getHeaders().getContentType();
        if (contentType == null) {
            return false;
        }
        if (MediaType.APPLICATION_SOAP12_XML.equalsTypeAndSubtype(contentType)) {
            return true;
        }
        if (MediaType.APPLICATION_ATOM_XML.equalsTypeAndSubtype(contentType)) {
            return true;
        }
        if (MediaType.TEXT_XML.equalsTypeAndSubtype(contentType) && httpMessage.getHeaders().containsKey("SOAPAction")) {
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

    }
}

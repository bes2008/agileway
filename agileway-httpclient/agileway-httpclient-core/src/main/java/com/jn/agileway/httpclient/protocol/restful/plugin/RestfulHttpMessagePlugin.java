package com.jn.agileway.httpclient.protocol.restful.plugin;

import com.jn.agileway.httpclient.core.HttpMessage;
import com.jn.agileway.httpclient.core.plugin.HttpMessageProtocolPlugin;
import com.jn.langx.util.net.mime.MediaType;

public class RestfulHttpMessagePlugin extends HttpMessageProtocolPlugin {
    @Override
    public boolean availableFor(HttpMessage httpMessage) {
        MediaType contentType = httpMessage.getHttpHeaders().getContentType();
        if (contentType != null) {
            if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(contentType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return "RestfulMessagePlugin";
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void initInternal() {
        this.requestPayloadWriters.add(new GeneralJsonHttpRequestWriter());
        this.responsePayloadReaders.add(new GeneralJsonHttpResponseReader());
    }
}

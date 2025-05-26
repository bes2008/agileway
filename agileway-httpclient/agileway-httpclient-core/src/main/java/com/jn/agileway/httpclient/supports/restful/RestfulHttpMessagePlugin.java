package com.jn.agileway.httpclient.supports.restful;

import com.jn.agileway.httpclient.core.HttpMessage;
import com.jn.agileway.httpclient.core.plugin.HttpMessagePlugin;
import com.jn.agileway.httpclient.supports.restful.content.GeneralJsonHttpRequestWriter;
import com.jn.agileway.httpclient.supports.restful.content.GeneralJsonHttpResponseReader;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.net.mime.MediaType;

public class RestfulHttpMessagePlugin extends HttpMessagePlugin {
    @Override
    public boolean availableFor(HttpMessage httpMessage) {
        MediaType contentType = httpMessage.getHeaders().getContentType();
        if (contentType != null) {
            if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(contentType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return "RestfulHttpMessagePlugin";
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
        this.requestContentWriters.add(new GeneralJsonHttpRequestWriter());
        this.responseContentReaders.add(new GeneralJsonHttpResponseReader());
    }
}

package com.jn.agileway.httpclient.xml.plugin;

import com.jn.agileway.httpclient.core.HttpMessage;
import com.jn.agileway.httpclient.core.plugin.HttpMessageProtocolPlugin;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.net.mime.MediaType;

import java.nio.charset.Charset;

public class XmlMessagePlugin extends HttpMessageProtocolPlugin {
    @Override
    public boolean availableFor(HttpMessage httpMessage) {
        MediaType contentType = httpMessage.getHttpHeaders().getContentType();
        if (contentType == null) {
            return false;
        }
        if (!MediaType.APPLICATION_XML.equalsTypeAndSubtype(contentType)) {
            return false;
        }
        Charset charset = contentType.getCharset();
        if (charset != null && !charset.equals(Charsets.UTF_8)) {
            return false;
        }
        return true;
    }

    @Override
    public String getName() {
        return "XmlMessagePlugin";
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 900;
    }

    @Override
    protected void initInternal() {
        this.requestPayloadWriters.add(new XmlHttpRequestWriter());
        this.responsePayloadReaders.add(new XmlHttpResponseReader());
    }
}

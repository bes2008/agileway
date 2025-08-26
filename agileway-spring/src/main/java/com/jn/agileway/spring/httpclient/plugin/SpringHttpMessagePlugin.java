package com.jn.agileway.spring.httpclient.plugin;

import com.jn.agileway.httpclient.core.HttpMessage;
import com.jn.agileway.httpclient.core.plugin.HttpMessageProtocolPlugin;

public class SpringHttpMessagePlugin extends HttpMessageProtocolPlugin {
    @Override
    protected void initInternal() {
        this.requestInterceptors.add(new SpringMultiValueMapInterceptor());
    }

    @Override
    public boolean availableFor(HttpMessage httpMessage) {
        return true;
    }

    @Override
    public String getName() {
        return "spring";
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 1100;
    }

    @Override
    public void destroy() {

    }
}

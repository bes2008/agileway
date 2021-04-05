package com.jn.agileway.web.filter.header;

import com.jn.agileway.web.request.handler.HttpRequestHandler;
import com.jn.agileway.web.request.handler.HttpRequestHandlerChain;
import com.jn.agileway.web.request.header.HttpResponseHeaderRule;
import com.jn.agileway.web.request.header.HttpResponseHeaderSetterFactory;
import com.jn.agileway.web.servlet.RR;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;

import java.util.List;

public class SetResponseHeaderHandler extends AbstractInitializable implements HttpRequestHandler {
    private SetResponseHeaderProperties config;
    private HttpRequestHandlerChain setters;

    public SetResponseHeaderHandler() {
    }


    public void setConfig(SetResponseHeaderProperties config) {
        this.config = config;
    }

    @Override
    protected void doInit() throws InitializationException {
        if (config != null && Objs.isNotEmpty(config.getRules())) {
            HttpRequestHandlerChain chain = new HttpRequestHandlerChain();

            List<HttpRequestHandler> setterList = Pipeline.of(config.getRules()).map(new Function<HttpResponseHeaderRule, HttpRequestHandler>() {
                @Override
                public HttpRequestHandler apply(HttpResponseHeaderRule rule) {
                    return new HttpResponseHeaderSetterFactory().get(rule);
                }
            }).asList();
            chain.setHandlers(setterList);
            this.setters = chain;
        }
    }

    @Override
    public void handle(RR rr) {
        if (config == null || Objs.isEmpty(setters)) {
            return;
        }

    }

    @Override
    public String toString() {
        return "Set-Response-Header";
    }
}

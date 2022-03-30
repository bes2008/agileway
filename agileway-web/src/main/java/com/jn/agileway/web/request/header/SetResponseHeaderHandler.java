package com.jn.agileway.web.request.header;

import com.jn.agileway.web.request.handler.HttpRequestHandler;
import com.jn.agileway.web.request.handler.HttpRequestHandlerChain;
import com.jn.agileway.web.servlet.RR;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;

import java.util.List;

public class SetResponseHeaderHandler extends AbstractInitializable implements HttpRequestHandler {
    private List<HttpResponseHeaderRule> rules;
    private HttpRequestHandlerChain setters;

    public SetResponseHeaderHandler() {
    }

    public void setRules(List<HttpResponseHeaderRule> rules) {
        this.rules = rules;
    }

    @Override
    protected void doInit() throws InitializationException {
        if (Objs.isNotEmpty(rules)) {
            HttpRequestHandlerChain chain = new HttpRequestHandlerChain();

            List<HttpRequestHandler> setterList = Pipeline.of(rules).map(new Function<HttpResponseHeaderRule, HttpRequestHandler>() {
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
        if (Objs.isEmpty(setters)) {
            return;
        }
        setters.handle(rr);
    }

    @Override
    public String toString() {
        return "Set-Response-Header";
    }
}

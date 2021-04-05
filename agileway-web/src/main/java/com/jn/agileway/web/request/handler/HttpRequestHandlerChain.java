package com.jn.agileway.web.request.handler;

import com.jn.agileway.web.request.header.HttpResponseHeaderSetter;
import com.jn.agileway.web.servlet.RR;
import com.jn.langx.util.EmptyEvalutible;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;

import java.util.List;

public class HttpRequestHandlerChain implements HttpRequestHandler, EmptyEvalutible {
    private List<HttpRequestHandler> handlers;

    public void setHandlers(List<HttpRequestHandler> handlers) {
        this.handlers = Pipeline.of(handlers).filter(new Predicate<HttpRequestHandler>() {
            @Override
            public boolean test(HttpRequestHandler handler) {
                return handler instanceof HttpResponseHeaderSetter;
            }
        }).asList();
    }

    @Override
    public boolean isEmpty() {
        return Objs.isEmpty(handlers);
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public void handle(final RR rr) {
        Collects.forEach(handlers, new Consumer<HttpRequestHandler>() {
            @Override
            public void accept(HttpRequestHandler handler) {
                handler.handle(rr);
            }
        });
    }
}

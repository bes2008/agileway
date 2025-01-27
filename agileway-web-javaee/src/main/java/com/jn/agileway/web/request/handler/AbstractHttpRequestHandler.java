package com.jn.agileway.web.request.handler;

import com.jn.agileway.http.rr.RR;
import com.jn.agileway.web.prediate.HttpRequestPredicateGroup;

public abstract class AbstractHttpRequestHandler implements HttpRequestHandler {
    private HttpRequestPredicateGroup predicates;

    public HttpRequestPredicateGroup getPredicates() {
        return predicates;
    }

    public void setPredicates(HttpRequestPredicateGroup predicates) {
        this.predicates = predicates;
    }

    protected boolean isMatch(RR rr) {
        return predicates == null || predicates.match(rr);
    }

    @Override
    public final void handle(RR rr) {
        if (isMatch(rr)) {
            internalHandle(rr);
        }
    }

    protected abstract void internalHandle(RR rr);
}

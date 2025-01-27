package com.jn.agileway.web.request.header;

import com.jn.agileway.http.rr.RR;
import com.jn.agileway.web.request.handler.AbstractHttpRequestHandler;

public class HttpResponseHeaderSetter extends AbstractHttpRequestHandler {
    private HttpResponseHeaderRule rule;

    public HttpResponseHeaderRule getRule() {
        return rule;
    }

    public void setRule(HttpResponseHeaderRule rule) {
        this.rule = rule;
    }

    @Override
    protected void internalHandle(RR rr) {
        rr.getResponse().addHeader(rule.getHeader(), rule.getValue());
    }

    @Override
    public String toString() {
        return "HttpResponseHeaderSetter{" +
                "header='" + rule.getHeader() + '\'' +
                '}';
    }
}

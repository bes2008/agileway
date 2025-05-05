package com.jn.agileway.web.request.header;

import com.jn.agileway.web.prediate.HttpRequestPredicateGroup;
import com.jn.agileway.web.prediate.HttpRequestPredicateGroupFactory;
import com.jn.langx.Factory;

public class HttpResponseHeaderSetterFactory implements Factory<HttpResponseHeaderRule, HttpResponseHeaderSetter> {
    @Override
    public HttpResponseHeaderSetter get(HttpResponseHeaderRule rule) {
        HttpResponseHeaderSetter setter = new HttpResponseHeaderSetter();
        HttpRequestPredicateGroup predicates = new HttpRequestPredicateGroupFactory().get(rule);
        setter.setRule(rule);
        setter.setPredicates(predicates);
        return setter;
    }
}

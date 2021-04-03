package com.jn.agileway.web.filter.waf.xcontenttype;

import com.jn.agileway.web.prediate.HttpRequestPredicateGroupProperties;

public class XContentTypeOptionsProperties {
    private boolean enabled;
    private HttpRequestPredicateGroupProperties predicates;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public HttpRequestPredicateGroupProperties getPredicates() {
        return predicates;
    }

    public void setPredicates(HttpRequestPredicateGroupProperties predicates) {
        this.predicates = predicates;
    }
}

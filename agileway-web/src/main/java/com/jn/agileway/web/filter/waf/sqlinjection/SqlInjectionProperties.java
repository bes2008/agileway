package com.jn.agileway.web.filter.waf.sqlinjection;

import com.jn.agileway.web.prediate.HttpRequestPredicateGroupProperties;

public class SqlInjectionProperties {
    private boolean enabled = false;
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

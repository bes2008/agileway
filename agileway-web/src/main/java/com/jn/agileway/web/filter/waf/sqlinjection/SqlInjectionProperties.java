package com.jn.agileway.web.filter.waf.sqlinjection;

import com.jn.agileway.web.prediate.HttpRequestPredicateConfigItems;

public class SqlInjectionProperties {
    private boolean enabled = false;
    private HttpRequestPredicateConfigItems predicates;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public HttpRequestPredicateConfigItems getPredicates() {
        return predicates;
    }

    public void setPredicates(HttpRequestPredicateConfigItems predicates) {
        this.predicates = predicates;
    }
}

package com.jn.agileway.web.security.sqlinjection;

import com.jn.agileway.web.prediate.HttpRequestPredicateConfigItems;

public class SqlInjectionProperties extends HttpRequestPredicateConfigItems {
    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}

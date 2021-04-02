package com.jn.agileway.web.filter.waf.sqlinjection;

public class SqlInjectionProperties {
    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

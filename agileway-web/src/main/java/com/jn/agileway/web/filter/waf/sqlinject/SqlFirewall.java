package com.jn.agileway.web.filter.waf.sqlinject;

import com.jn.agileway.web.filter.waf.WAF;

public class SqlFirewall extends WAF {
    private SqlInjectProperties config;

    public SqlInjectProperties getConfig() {
        return config;
    }

    public void setConfig(SqlInjectProperties config) {
        this.config = config;
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        config.setEnabled(enabled);
    }
}

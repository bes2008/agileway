package com.jn.agileway.web.filter.waf.xss;

import com.jn.agileway.web.filter.waf.WAF;

public class XssFirewall extends WAF {
    private XssProperties config;

    public String getContentSecurityPolicy() {
        return config.getContentSecurityPolicy();
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        config.setEnabled(enabled);
    }

    public XssProperties getConfig() {
        return config;
    }

    public void setConfig(XssProperties config) {
        this.config = config;
    }

}

package com.jn.agileway.web.filter.waf.xss;

import com.jn.agileway.web.filter.waf.WAF;

public class XssFirewall extends WAF {
    private XssProperties properties;

    public String getContentSecurityPolicy() {
        return properties.getContentSecurityPolicy();
    }

    public XssProperties getProperties() {
        return properties;
    }

    public void setProperties(XssProperties properties) {
        this.properties = properties;
    }

}

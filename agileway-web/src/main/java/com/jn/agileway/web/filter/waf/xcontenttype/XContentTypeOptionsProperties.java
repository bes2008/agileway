package com.jn.agileway.web.filter.waf.xcontenttype;

import com.jn.agileway.web.prediate.HttpRequestPredicateConfigItems;

public class XContentTypeOptionsProperties extends HttpRequestPredicateConfigItems {
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}

package com.jn.agileway.web.request.header;

import com.jn.agileway.web.request.header.HttpResponseHeaderRule;

import java.util.List;

public class SetResponseHeaderProperties {
    private List<HttpResponseHeaderRule> rules;

    public List<HttpResponseHeaderRule> getRules() {
        return rules;
    }

    public void setRules(List<HttpResponseHeaderRule> rules) {
        this.rules = rules;
    }
}

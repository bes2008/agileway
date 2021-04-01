package com.jn.agileway.web.filter.waf.sqlinject;

import com.jn.agileway.web.filter.waf.WAFHandler;
import com.jn.agileway.web.prediates.HttpRequestPredicate;

import java.util.List;

public class SqlInjectFirewall {
    private boolean enable;

    List<HttpRequestPredicate> predicate;
    List<WAFHandler> wafHandlers;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}

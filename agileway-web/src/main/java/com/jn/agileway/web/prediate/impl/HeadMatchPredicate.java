package com.jn.agileway.web.prediate.impl;

import com.jn.agileway.web.prediate.HttpRequestPredicate;
import com.jn.agileway.web.servlet.RR;

public class HeadMatchPredicate implements HttpRequestPredicate {
    private String header;
    private String valuePattern;
    @Override
    public boolean test(RR rr) {
        return false;
    }
}

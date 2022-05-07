package com.jn.agileway.web.prediate.impl;

import com.jn.agileway.web.prediate.HttpRequestPredicate;
import com.jn.agileway.http.rr.RR;
import com.jn.langx.util.pattern.PatternMatcher;

public class HostMatchPredicate implements HttpRequestPredicate {
    private PatternMatcher matcher;

    public PatternMatcher getMatcher() {
        return matcher;
    }

    public void setMatcher(PatternMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean test(RR holder) {
        return matcher == null || matcher.match(holder.getRequest().getRemoteHost());
    }
}

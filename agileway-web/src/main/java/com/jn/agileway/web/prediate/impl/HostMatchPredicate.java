package com.jn.agileway.web.prediate.impl;

import com.jn.agileway.web.prediate.HttpRequestPredicate;
import com.jn.agileway.web.servlet.RR;
import com.jn.langx.util.pattern.patternset.RegexpPatternSetMatcher;

public class HostMatchPredicate implements HttpRequestPredicate {
    private RegexpPatternSetMatcher matcher;

    @Override
    public boolean test(RR holder) {
        return false;
    }
}

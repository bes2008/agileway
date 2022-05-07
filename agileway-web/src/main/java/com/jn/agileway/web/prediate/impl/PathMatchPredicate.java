package com.jn.agileway.web.prediate.impl;

import com.jn.agileway.web.prediate.HttpRequestPredicate;
import com.jn.agileway.http.rr.RR;
import com.jn.langx.util.Objs;
import com.jn.langx.util.pattern.patternset.AntPathMatcher;

public class PathMatchPredicate implements HttpRequestPredicate {

    private AntPathMatcher includePathMatcher;
    private AntPathMatcher excludePathMatcher;

    public PathMatchPredicate() {
    }

    public PathMatchPredicate(String includes) {
        this(includes, null);
    }

    public PathMatchPredicate(String includes, String excludes) {
        setIncludePatterns(includes);
        setExcludePatterns(excludes);
    }

    public void setIncludePatterns(String patternsExpression) {
        if (Objs.isNotEmpty(patternsExpression)) {
            this.includePathMatcher = new AntPathMatcher((String) null, patternsExpression);
        }
    }

    public void setExcludePatterns(String patternsExpression) {
        if (Objs.isNotEmpty(patternsExpression)) {
            this.excludePathMatcher = new AntPathMatcher((String) null, patternsExpression);
        }
    }

    @Override
    public boolean test(RR holder) {
        if (includePathMatcher == null) {
            return false;
        }
        String requestUri = holder.getRequest().getRequestURI();
        if (includePathMatcher.match(requestUri)) {
            if (excludePathMatcher == null || !excludePathMatcher.match(requestUri)) {
                return true;
            }
        }
        return false;
    }
}

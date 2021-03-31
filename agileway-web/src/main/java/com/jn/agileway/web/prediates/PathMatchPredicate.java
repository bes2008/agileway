package com.jn.agileway.web.prediates;

import com.jn.agileway.web.servlet.RR;
import com.jn.langx.util.pattern.patternset.AntPathMatcher;

public class PathMatchPredicate implements HttpRequestPredicate {

    private AntPathMatcher includePathMatcher;
    private AntPathMatcher excludePathMatcher;

    public void setIncludePatterns(String patternsExpression) {
        this.includePathMatcher = new AntPathMatcher((String) null, patternsExpression);
    }

    public void setExcludePatterns(String patternsExpression) {
        this.excludePathMatcher = new AntPathMatcher((String) null, patternsExpression);
    }

    @Override
    public boolean test(RR holder) {
        String requestUri = holder.getRequest().getRequestURI();
        if (includePathMatcher == null || includePathMatcher.match(requestUri)) {
            if (excludePathMatcher == null || !excludePathMatcher.match(requestUri)) {
                return true;
            }
        }
        return false;
    }
}

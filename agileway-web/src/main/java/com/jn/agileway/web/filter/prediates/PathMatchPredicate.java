package com.jn.agileway.web.filter.prediates;

import com.jn.langx.util.pattern.patternset.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;

public class PathMatchPredicate implements HttpServletRequestPredicate {

    private AntPathMatcher includePathMatcher;
    private AntPathMatcher excludePathMatcher;

    public void setIncludePatterns(String patternsExpression) {
        this.includePathMatcher = new AntPathMatcher((String) null, patternsExpression);
    }

    public void setExcludePatterns(String patternsExpression) {
        this.excludePathMatcher = new AntPathMatcher((String) null, patternsExpression);
    }

    @Override
    public boolean test(HttpServletRequest request) {
        if (includePathMatcher == null) {
            return false;
        }

        String requestUri = request.getRequestURI();
        if (includePathMatcher.match(requestUri)) {
            if (excludePathMatcher == null || !excludePathMatcher.match(requestUri)) {
                return true;
            }
        }
        return false;
    }
}

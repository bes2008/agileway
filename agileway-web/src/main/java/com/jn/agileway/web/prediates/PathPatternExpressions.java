package com.jn.agileway.web.prediates;

/**
 * @see com.jn.langx.util.pattern.patternset.AntPathMatcher
 */
public class PathPatternExpressions {
    private String includes;
    private String excludes;

    public PathPatternExpressions() {

    }

    public PathPatternExpressions(String includes) {
        setIncludes(includes);
    }

    public String getIncludes() {
        return includes;
    }

    public void setIncludes(String includes) {
        this.includes = includes;
    }

    public String getExcludes() {
        return excludes;
    }

    public void setExcludes(String excludes) {
        this.excludes = excludes;
    }
}

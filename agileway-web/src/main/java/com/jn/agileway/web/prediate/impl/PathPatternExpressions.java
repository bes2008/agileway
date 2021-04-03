package com.jn.agileway.web.prediate.impl;

import com.jn.langx.util.Objs;

import java.util.HashMap;
import java.util.Map;

/**
 * @see com.jn.langx.util.pattern.patternset.AntPathMatcher
 */
public class PathPatternExpressions {
    static final String INCLUDES = "includes";
    static final String EXCLUDES = "excludes";

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

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();
        if (Objs.isNotEmpty(includes)) {
            map.put(INCLUDES, includes);
        }
        if (Objs.isNotEmpty(includes)) {
            map.put(EXCLUDES, excludes);
        }
        return map;
    }
}

package com.jn.agileway.web.prediates;

import com.jn.langx.util.Objs;

import java.util.List;
import java.util.Map;

public class HttpRequestPredicateGroupProperties {

    private List<String> methods;

    /**
     * @see PathMatchPredicateFactory
     * <p>
     * 可选类型：Map<String,String>,PathPatternExpressions
     */
    private Map<String, String> paths = new PathPatternExpressions("/**").toMap();

    public Map<String, String> getPaths() {
        return paths;
    }

    public void setPaths(Map<String, String> paths) {
        this.paths = paths;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        if (Objs.isNotEmpty(methods)) {
            this.methods = methods;
        }
    }
}

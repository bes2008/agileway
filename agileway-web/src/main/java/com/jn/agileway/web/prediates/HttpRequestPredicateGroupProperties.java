package com.jn.agileway.web.prediates;

import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Pipeline;

import java.util.List;

public class HttpRequestPredicateGroupProperties {

    private String[] methods;

    /**
     * @see PathMatchPredicateFactory
     * <p>
     * 可选类型：Map<String,String>,PathPatternExpressions
     */
    private PathPatternExpressions paths = new PathPatternExpressions("/**");

    public PathPatternExpressions getPaths() {
        return paths;
    }

    public void setPaths(PathPatternExpressions paths) {
        this.paths = paths;
    }

    public List<String> getMethods() {
        return Pipeline.of(this.methods).clearNulls().asList();
    }

    public void setMethods(String[] methods) {
        if (Objs.isNotEmpty(methods)) {
            this.methods = methods;
        }
    }
}

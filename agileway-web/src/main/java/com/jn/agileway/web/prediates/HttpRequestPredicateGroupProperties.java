package com.jn.agileway.web.prediates;

import com.jn.langx.annotation.Name;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Pipeline;

import java.util.List;

import static com.jn.agileway.web.prediates.HttpRequestPredicates.PREDICATE_TYPE_METHOD;
import static com.jn.agileway.web.prediates.HttpRequestPredicates.PREDICATE_TYPE_PATH;

/**
 * 这里面的每一个字段，都代表一个类限制条件。如果为 null ，则代表无此项限制。
 * 如果所有字段都是 null，就代表没有任何的限制，也就是任何请求都匹配的
 */

public class HttpRequestPredicateGroupProperties {

    @Name(PREDICATE_TYPE_METHOD)
    private String[] methods;

    /**
     * @see PathMatchPredicateFactory
     * <p>
     * 可选类型：Map<String,String>,PathPatternExpressions
     */
    @Name(PREDICATE_TYPE_PATH)
    private PathPatternExpressions paths;

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

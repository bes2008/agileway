package com.jn.agileway.web.prediates;

import com.jn.langx.util.Objs;

import java.util.List;

public class MethodPredicateFactory extends HttpRequestPredicateFactory {

    public MethodPredicateFactory() {
        setName("method");
    }

    @Override
    public HttpRequestPredicate get(Object args) {
        MethodPredicate predicate = new MethodPredicate();
        if (Objs.isNotEmpty(args)) {
            List<String> methods = HttpRequestPredicates.toStringList(args, ", ");
        }
        return predicate;
    }

}

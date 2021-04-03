package com.jn.agileway.web.prediate.impl;

import com.jn.agileway.web.prediate.HttpRequestPredicate;
import com.jn.agileway.web.prediate.HttpRequestPredicateFactory;
import com.jn.agileway.web.prediate.HttpRequestPredicates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Functions;

import java.util.List;

public class MethodPredicateFactory extends HttpRequestPredicateFactory {

    public MethodPredicateFactory() {
        setName(HttpRequestPredicates.PREDICATE_TYPE_METHOD);
    }

    @Override
    public HttpRequestPredicate get(String args) {
        MethodPredicate predicate = new MethodPredicate();
        if (Objs.isNotEmpty(args)) {
            List<String> methods = HttpRequestPredicates.toStringList(args, ", ");
            methods = Pipeline.of(methods)
                    .clearNulls()
                    .map(Functions.toUpperCase())
                    .asList();
            predicate.setMethods(methods);
        }
        return predicate;
    }

}

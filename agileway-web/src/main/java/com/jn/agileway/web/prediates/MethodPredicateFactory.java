package com.jn.agileway.web.prediates;

import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Functions;

import java.util.List;

public class MethodPredicateFactory extends HttpRequestPredicateFactory {

    public MethodPredicateFactory() {
        setName("methods");
    }

    @Override
    public HttpRequestPredicate get(Object args) {
        MethodPredicate predicate = new MethodPredicate();
        if (Objs.isNotEmpty(args)) {
            List<String> methods = null;
            if (args instanceof String) {
                methods = HttpRequestPredicates.toStringList(args, ", ");
            } else {
                methods = Pipeline.of(Collects.asIterable(args))
                        .clearNulls()
                        .map(Functions.toStringFunction())
                        .asList();
            }
            predicate.setMethods(methods);
        }
        return predicate;
    }

}

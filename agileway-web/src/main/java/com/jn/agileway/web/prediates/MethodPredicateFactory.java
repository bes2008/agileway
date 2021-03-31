package com.jn.agileway.web.prediates;

import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Arrs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Predicate;

import java.util.Collection;
import java.util.List;

public class MethodPredicateFactory extends HttpRequestPredicateFactory {
    @Override
    public HttpRequestPredicate get(Object args) {
        MethodPredicate predicate = new MethodPredicate();
        if (Objs.isNotEmpty(args)) {
            if (args instanceof String) {
                String[] methods = Strings.split((String) args, ", ");
                predicate.setMethods(methods);
            } else if (Arrs.isArray(args) || args instanceof Collection) {
                List<String> methods = Pipeline.of(Collects.asIterable(args)).filter(new Predicate<Object>() {
                    @Override
                    public boolean test(Object o) {
                        return o instanceof String;
                    }
                }).map(Functions.toStringFunction()).asList();
                predicate.setMethods(methods);
            }
        }
        return predicate;
    }

}

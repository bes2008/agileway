package com.jn.agileway.web.prediate.impl;

import com.jn.agileway.web.prediate.HttpRequestPredicateFactory;
import com.jn.agileway.web.prediate.HttpRequestPredicates;
import com.jn.langx.annotation.Name;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Predicate;

import java.util.List;

@Name("Method")
public class MethodPredicateFactory extends HttpRequestPredicateFactory<MethodPredicate> {

    public MethodPredicateFactory() {
        setName(HttpRequestPredicates.PREDICATE_KEY_METHOD);
    }

    @Override
    public MethodPredicate get(String configuration) {
        MethodPredicate predicate = new MethodPredicate();
        if (Objs.isNotEmpty(configuration)) {
            List<String> methods = Collects.asList(Strings.split(configuration, ", "));
            methods = Pipeline.of(methods)
                    .map(Functions.toUpperCase())
                    .filter(new Predicate<String>() {
                        @Override
                        public boolean test(String value) {
                            return MethodPredicate.DEFAULT_METHODS.contains(value);
                        }
                    })
                    .asList();
            predicate.setMethods(methods);
        }
        return predicate;
    }

}

package com.jn.agileway.web.prediates;

import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Arrs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Predicate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PathMatchPredicateFactory extends HttpRequestPredicateFactory {
    @Override
    public HttpRequestPredicate get(Object o) {
        final PathMatchPredicate predicate = new PathMatchPredicate();

        if (Objs.isNotEmpty(o)) {
            if (Arrs.isArray(o) || o instanceof Collection) {
                List<String> expressions = Pipeline.of(Collects.asIterable(o)).filter(new Predicate<Object>() {
                    @Override
                    public boolean test(Object o) {
                        return o instanceof String;
                    }
                }).map(Functions.toStringFunction()).asList();
                if (Objs.isNotEmpty(expressions)) {
                    String includePatternExpression = expressions.get(0);
                    predicate.setIncludePatterns(includePatternExpression);
                    if (expressions.size() > 1) {
                        predicate.setExcludePatterns(expressions.get(1));
                    }
                }

            } else if (o instanceof Map) {
                Collects.forEach((Map) o, new Consumer2<Object, Object>() {
                    @Override
                    public void accept(Object key, Object value) {
                        if ("include".equals(key) && value instanceof String) {
                            predicate.setIncludePatterns((String) value);
                        }
                        if ("exclude".equals(key) && value instanceof String) {
                            predicate.setExcludePatterns((String) value);
                        }
                    }
                });
            }
        }

        return predicate;
    }
}

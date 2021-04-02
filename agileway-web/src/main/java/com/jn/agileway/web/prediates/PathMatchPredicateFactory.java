package com.jn.agileway.web.prediates;

import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Arrs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.jn.agileway.web.prediates.PathPatternExpressions.EXCLUDES;
import static com.jn.agileway.web.prediates.PathPatternExpressions.INCLUDES;

public class PathMatchPredicateFactory extends HttpRequestPredicateFactory {

    public PathMatchPredicateFactory() {
        setName("path");
    }

    @Override
    public HttpRequestPredicate get(Object o) {
        PathMatchPredicate predicate = null;

        if (Objs.isNotEmpty(o)) {
            final PathPatternExpressions path = new PathPatternExpressions();
            if (Arrs.isArray(o) || o instanceof Collection) {
                List<String> expressions = HttpRequestPredicates.toStringList(o, null);
                if (Objs.isNotEmpty(expressions)) {
                    String includePatternExpression = expressions.get(0);
                    path.setIncludes(includePatternExpression);
                    if (expressions.size() > 1) {
                        path.setExcludes(expressions.get(1));
                    }
                }

            } else if (o instanceof Map) {
                Collects.forEach((Map) o, new Consumer2<Object, Object>() {
                    @Override
                    public void accept(Object key, Object value) {
                        if (INCLUDES.equals(key)) {
                            if (value instanceof String) {
                                path.setIncludes((String) value);
                            } else if (Arrs.isArray(value) || value instanceof Collection) {
                                List<String> expressions = HttpRequestPredicates.toStringList(value, null);
                                if (Objs.isNotEmpty(expressions)) {
                                    String patternSet = Strings.join(";", expressions);
                                    path.setIncludes(patternSet);
                                }
                            }
                        }
                        if (EXCLUDES.equals(key)) {
                            if (value instanceof String) {
                                path.setExcludes((String) value);
                            } else if (Arrs.isArray(value) || value instanceof Collection) {
                                List<String> expressions = HttpRequestPredicates.toStringList(value, null);
                                if (Objs.isNotEmpty(expressions)) {
                                    String patternSet = Strings.join(";", expressions);
                                    path.setExcludes(patternSet);
                                }
                            }
                        }
                    }
                });
            }

            predicate = new PathMatchPredicate(path);
        }

        return predicate;
    }
}

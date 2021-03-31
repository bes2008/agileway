package com.jn.agileway.web.prediates;

import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Arrs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PathMatchPredicateFactory extends HttpRequestPredicateFactory {
    @Override
    public HttpRequestPredicate get(Object o) {
        final PathMatchPredicate predicate = new PathMatchPredicate();

        if (Objs.isNotEmpty(o)) {
            if (Arrs.isArray(o) || o instanceof Collection) {
                List<String> expressions = HttpRequestPredicates.toStringList(o, null);
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
                        if ("include".equals(key)) {
                            if (value instanceof String) {
                                predicate.setIncludePatterns((String) value);
                            } else if (Arrs.isArray(value) || value instanceof Collection) {
                                List<String> expressions = HttpRequestPredicates.toStringList(value, null);
                                if (Objs.isNotEmpty(expressions)) {
                                    String patternSet = Strings.join(";", expressions);
                                    predicate.setIncludePatterns(patternSet);
                                }
                            }
                        }
                        if ("exclude".equals(key)) {
                            if (value instanceof String) {
                                predicate.setExcludePatterns((String) value);
                            } else if (Arrs.isArray(value) || value instanceof Collection) {
                                List<String> expressions = HttpRequestPredicates.toStringList(value, null);
                                if (Objs.isNotEmpty(expressions)) {
                                    String patternSet = Strings.join(";", expressions);
                                    predicate.setExcludePatterns(patternSet);
                                }
                            }
                        }
                    }
                });
            }
        }

        return predicate;
    }
}

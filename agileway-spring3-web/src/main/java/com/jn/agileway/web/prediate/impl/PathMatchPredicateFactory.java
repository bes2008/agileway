package com.jn.agileway.web.prediate.impl;

import com.jn.agileway.web.prediate.HttpRequestPredicateFactory;
import com.jn.agileway.web.prediate.HttpRequestPredicates;
import com.jn.langx.annotation.Name;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;

import java.util.List;

@Name("Path")
public class PathMatchPredicateFactory extends HttpRequestPredicateFactory<PathMatchPredicate> {

    public PathMatchPredicateFactory() {
        setName(HttpRequestPredicates.PREDICATE_KEY_PATH);
    }

    @Override
    public PathMatchPredicate get(String configuration) {
        PathMatchPredicate predicate = new PathMatchPredicate();

        if (Strings.isNotBlank(configuration)) {
            List<String> expressions = Collects.asList(Strings.split(configuration, " "));
            if (Objs.isNotEmpty(expressions)) {
                String includePatternExpression = expressions.get(0);
                predicate.setIncludePatterns(includePatternExpression);
                if (expressions.size() > 1) {
                    predicate.setExcludePatterns(expressions.get(1));
                }
            }
        }
        return predicate;
    }
}

package com.jn.agileway.web.prediate.impl;

import com.jn.agileway.web.prediate.HttpRequestPredicate;
import com.jn.agileway.web.prediate.HttpRequestPredicateFactory;
import com.jn.agileway.web.prediate.HttpRequestPredicates;
import com.jn.langx.util.Strings;
import com.jn.langx.util.pattern.patternset.GenericPatternSetExpressionParser;
import com.jn.langx.util.pattern.patternset.RegexpPatternSetMatcher;
import com.jn.langx.util.pattern.patternset.StringPatternEntry;

public class HostMatchPredicateFactory extends HttpRequestPredicateFactory {
    public HostMatchPredicateFactory() {
        setName(HttpRequestPredicates.PREDICATE_KEY_HOSTS);
    }

    @Override
    public HttpRequestPredicate get(String configuration) {
        if (Strings.isBlank(configuration)) {
            return null;
        }
        configuration = configuration.trim();
        RegexpPatternSetMatcher matcher = new RegexpPatternSetMatcher(new GenericPatternSetExpressionParser<StringPatternEntry>(new StringPatternEntry.Factory()), null, configuration);
        HostMatchPredicate predicate = new HostMatchPredicate();
        predicate.setMatcher(matcher);
        return predicate;
    }
}

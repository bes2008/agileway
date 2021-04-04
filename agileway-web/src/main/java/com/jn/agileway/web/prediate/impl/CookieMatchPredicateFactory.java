package com.jn.agileway.web.prediate.impl;

import com.jn.agileway.web.prediate.HttpRequestPredicateFactory;
import com.jn.langx.util.Strings;
import com.jn.langx.util.pattern.patternset.GenericPatternSetExpressionParser;
import com.jn.langx.util.pattern.patternset.RegexpPatternSetMatcher;
import com.jn.langx.util.pattern.patternset.StringPatternEntry;

public class CookieMatchPredicateFactory extends HttpRequestPredicateFactory<CookieMatchPredicate> {
    @Override
    public CookieMatchPredicate get(String configuration) {
        CookieMatchPredicate predicate = null;
        if (Strings.isNotBlank(configuration)) {
            String[] segments = Strings.split(configuration, ", ");
            if (segments.length > 0) {
                predicate = new CookieMatchPredicate();
                predicate.setName(segments[0]);

                if (segments.length > 1) {
                    RegexpPatternSetMatcher matcher = new RegexpPatternSetMatcher(new GenericPatternSetExpressionParser<StringPatternEntry>(new StringPatternEntry.Factory()), null, segments[1]);
                    predicate.setMatcher(matcher);
                }
            }
        }
        return predicate;
    }
}

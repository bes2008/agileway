package com.jn.agileway.vfs.filter;

import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.predicate.StringEqualsPredicate;

public class FilenameEqualsFileFilter extends FilenamePredicateFilter {

    public FilenameEqualsFileFilter(String expected) {
        this(true, expected);
    }

    public FilenameEqualsFileFilter(final boolean ignore, String... expecteds) {
        super(Functions.anyPredicate(Pipeline.of(expecteds).map(new Function<String, Predicate<String>>() {
            @Override
            public Predicate<String> apply(String expected) {
                return new StringEqualsPredicate(expected, ignore);
            }
        }).asList()));
    }


}

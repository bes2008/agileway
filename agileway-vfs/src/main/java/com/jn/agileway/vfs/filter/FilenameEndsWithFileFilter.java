package com.jn.agileway.vfs.filter;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.predicate.StringEndsWithPredicate;

import java.util.List;

public class FilenameEndsWithFileFilter extends FilenamePredicateFilter {
    public FilenameEndsWithFileFilter(boolean ignoreCase, String suffix) {
        this(ignoreCase, Collects.asList(suffix));
    }

    public FilenameEndsWithFileFilter(boolean ignoreCase, String... suffixes) {
        this(ignoreCase, Collects.asList(suffixes));
    }

    public FilenameEndsWithFileFilter(boolean ignoreCase, List<String> suffixes) {
        super(new StringEndsWithPredicate(ignoreCase, suffixes));
    }

}

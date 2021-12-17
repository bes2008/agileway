package com.jn.agileway.vfs.filter;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.predicate.StringEndsWithPredicate;

import java.util.List;

public class FilenameSuffixFilter extends FilenamePredicateFilter {
    public FilenameSuffixFilter(boolean ignoreCase, String suffix) {
        this(ignoreCase, Collects.asList(suffix));
    }

    public FilenameSuffixFilter(boolean ignoreCase, String... suffixes) {
        this(ignoreCase, Collects.asList(suffixes));
    }

    public FilenameSuffixFilter(boolean ignoreCase, List<String> suffixes) {
        super(new StringEndsWithPredicate(ignoreCase, suffixes));
    }


}

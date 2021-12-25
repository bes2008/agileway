package com.jn.agileway.vfs.filter;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.predicate.StringStartsWithPredicate;

import java.util.List;

public class FilenamePrefixFileFilter extends FilenamePredicateFilter {
    public FilenamePrefixFileFilter(boolean ignoreCase, String prefix) {
        this(ignoreCase, Collects.asList(prefix));
    }

    public FilenamePrefixFileFilter(boolean ignoreCase, String... prefixes) {
        this(ignoreCase, Collects.asList(prefixes));
    }

    public FilenamePrefixFileFilter(boolean ignoreCase, List<String> prefixes) {
        super(new StringStartsWithPredicate(ignoreCase, prefixes));
    }


}
